package com.example.organizadoracademico.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.mapper.toDomain as remoteToDomain
import com.example.organizadoracademico.data.sync.ImagenDeletePayload
import com.example.organizadoracademico.data.sync.SyncAction
import com.example.organizadoracademico.data.sync.SyncEntityType
import com.example.organizadoracademico.data.sync.SyncScheduler
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ImagenRepositoryImpl(
    private val dao: ImagenDao,
    private val apiService: ApiService,
    private val context: Context,
    private val syncQueueDao: SyncQueueDao,
    private val syncScheduler: SyncScheduler
) : IImagenRepository {

    private val apiHost = "https://apimovil-production-b302.up.railway.app"
    private val gson = Gson()

    // 1. Filtramos localmente por materia Y usuario
    override fun getImagenesByMateria(materiaId: Int, userId: Int): Flow<List<Imagen>> {
        CoroutineScope(Dispatchers.IO).launch {
            syncImagenes(materiaId, userId)
            syncScheduler.scheduleNow()
        }

        return dao.getByMateria(materiaId, userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getImagenById(id: Int): Imagen? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertImagen(imagen: Imagen) {
        val localId = dao.insertAndReturnId(imagen.toEntity()).toInt()
        syncQueueDao.insert(
            SyncQueueEntity(
                entityType = SyncEntityType.IMAGEN,
                action = SyncAction.CREATE,
                entityLocalId = localId
            )
        )
        syncScheduler.scheduleNow()
    }

    override suspend fun updateNota(id: Int, nota: String) {
        // Endpoint remoto no disponible en API actual; mantenemos edición local.
        dao.updateNota(id, nota)
    }

    override suspend fun toggleFavorita(id: Int, favorita: Boolean) {
        // Endpoint remoto no disponible en API actual; mantenemos edición local.
        dao.toggleFavorita(id, favorita)
    }

    override suspend fun deleteImagen(id: Int) {
        val imagen = dao.getById(id)
        syncQueueDao.deleteByEntity(SyncEntityType.IMAGEN, id)

        if (imagen?.remoteId != null) {
            syncQueueDao.insert(
                SyncQueueEntity(
                    entityType = SyncEntityType.IMAGEN,
                    action = SyncAction.DELETE,
                    entityLocalId = id,
                    payload = gson.toJson(ImagenDeletePayload(remoteId = imagen.remoteId))
                )
            )
        }

        dao.deleteById(id)
        syncScheduler.scheduleNow()
    }

    private suspend fun syncImagenes(materiaId: Int, userId: Int) {
        try {
            val response = apiService.getImagenes(materiaId)
            if (!response.isSuccessful) return

            val localImages = dao.getByMateriaOnce(materiaId, userId)
            val localByRemoteId = localImages
                .filter { it.remoteId != null }
                .associateBy { it.remoteId }

            response.body().orEmpty()
                .filter { it.usuarioId == userId }
                .forEach { dto ->
                    val existing = localByRemoteId[dto.id]
                    val remoteUrl = normalizeUri(dto.uri)
                    val merged = if (existing != null) {
                        // Registro local ya vinculado: actualizar datos remotos
                        // Si tiene URI local válida la conservamos, si no usamos la remota
                        val keepLocalUri = isLocalUri(existing.uri)
                        existing.copy(
                            materiaId = dto.materiaId,
                            usuarioId = dto.usuarioId,
                            nota = dto.nota ?: existing.nota,
                            fecha = dto.fecha,
                            favorita = dto.favorita,
                            uri = if (keepLocalUri) existing.uri else remoteUrl,
                            remoteId = dto.id
                        )
                    } else {
                        // Registro nuevo de la nube: id = 0 para que Room
                        // autogenere un ID local que NO pise registros pendientes
                        dto.remoteToDomain()
                            .copy(uri = remoteUrl)
                            .toEntity()
                            .copy(id = 0, remoteId = dto.id)
                    }
                    try {
                        dao.insert(merged)
                    } catch (_: Exception) {
                        // FK constraint: materia/usuario aún no sincronizado localmente
                    }
                }
        } catch (_: Exception) {
        }
    }

    private fun buildFilePart(uriString: String): MultipartBody.Part? {
        val uri = Uri.parse(uriString)
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

        return when (uri.scheme) {
            "content" -> {
                val input = context.contentResolver.openInputStream(uri) ?: return null
                val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                input.use { inStream ->
                    FileOutputStream(tempFile).use { out ->
                        inStream.copyTo(out)
                    }
                }

                val fileName = queryFileName(uri) ?: tempFile.name
                val requestBody = tempFile.asRequestBody(mimeType.toMediaType())
                MultipartBody.Part.createFormData("file", fileName, requestBody)
            }

            "file", null -> {
                val file = File(uri.path ?: uriString)
                if (!file.exists()) return null
                val requestBody = file.asRequestBody(mimeType.toMediaType())
                MultipartBody.Part.createFormData("file", file.name, requestBody)
            }

            else -> null
        }
    }

    private fun queryFileName(uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun normalizeUri(uri: String): String {
        if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("content://") || uri.startsWith("file://")) {
            return uri
        }
        return if (uri.startsWith('/')) "$apiHost$uri" else "$apiHost/$uri"
    }

    private fun isLocalUri(uri: String): Boolean {
        return uri.startsWith("content://") || uri.startsWith("file://") || (!uri.startsWith("http://") && !uri.startsWith("https://"))
    }
}