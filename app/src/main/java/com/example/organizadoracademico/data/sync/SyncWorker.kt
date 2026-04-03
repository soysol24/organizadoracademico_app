package com.example.organizadoracademico.data.sync

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.entities.HorarioEntity
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.mapper.toCreateRequestDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.context.GlobalContext
import java.io.File
import java.io.FileOutputStream

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val koin = GlobalContext.get()
    private val apiService: ApiService = koin.get()
    private val imagenDao: ImagenDao = koin.get()
    private val horarioDao: HorarioDao = koin.get()
    private val syncQueueDao: SyncQueueDao = koin.get()
    private val gson = Gson()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val items = syncQueueDao.getPending(limit = 40)
        if (items.isEmpty()) return@withContext Result.success()

        var shouldRetry = false

        for (item in items) {
            val success = try {
                processItem(item)
            } catch (_: Exception) {
                false
            }

            if (success) {
                syncQueueDao.deleteById(item.id)
            } else {
                syncQueueDao.incrementRetry(item.id)
                shouldRetry = true
            }
        }

        if (shouldRetry) Result.retry() else Result.success()
    }

    private suspend fun processItem(item: SyncQueueEntity): Boolean {
        return when (item.entityType) {
            SyncEntityType.IMAGEN -> processImagen(item)
            SyncEntityType.HORARIO -> processHorario(item)
            else -> true
        }
    }

    private suspend fun processImagen(item: SyncQueueEntity): Boolean {
        return when (item.action) {
            SyncAction.CREATE -> {
                val imagen = imagenDao.getById(item.entityLocalId) ?: return true
                if (imagen.remoteId != null) return true

                val filePart = buildFilePart(imagen.uri) ?: return false
                val materiaIdPart = imagen.materiaId.toString().toRequestBody("text/plain".toMediaType())
                val notaPart = imagen.nota?.toRequestBody("text/plain".toMediaType())

                val response = apiService.uploadImagen(
                    file = filePart,
                    materiaId = materiaIdPart,
                    nota = notaPart
                )

                if (!response.isSuccessful) return false
                val dto = response.body() ?: return false

                imagenDao.insert(
                    imagen.copy(
                        remoteId = dto.id,
                        nota = dto.nota ?: imagen.nota,
                        favorita = dto.favorita
                    )
                )
                true
            }

            SyncAction.DELETE -> {
                val payload = item.payload?.let { gson.fromJson(it, ImagenDeletePayload::class.java) } ?: return true
                val response = apiService.deleteImagen(payload.remoteId)
                response.isSuccessful
            }

            else -> true
        }
    }

    private suspend fun processHorario(item: SyncQueueEntity): Boolean {
        return when (item.action) {
            SyncAction.CREATE -> {
                val horario = horarioDao.getById(item.entityLocalId) ?: return true
                if (horario.remoteId != null) return true

                val response = apiService.createHorario(horario.toDomain().toCreateRequestDto())
                if (!response.isSuccessful) return false
                val dto = response.body() ?: return false

                horarioDao.insert(horario.copy(remoteId = dto.id))
                true
            }

            SyncAction.DELETE -> {
                val payload = item.payload?.let { gson.fromJson(it, HorarioDeletePayload::class.java) } ?: return true
                val response = apiService.deleteHorario(payload.remoteId)
                response.isSuccessful
            }

            else -> true
        }
    }

    private fun buildFilePart(uriString: String): MultipartBody.Part? {
        val uri = Uri.parse(uriString)
        val mimeType = applicationContext.contentResolver.getType(uri) ?: "image/jpeg"

        return when (uri.scheme) {
            "content" -> {
                val input = applicationContext.contentResolver.openInputStream(uri) ?: return null
                val tempFile = File.createTempFile("upload_", ".jpg", applicationContext.cacheDir)
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
        applicationContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun HorarioEntity.toDomain() = com.example.organizadoracademico.domain.model.Horario(
        id = id,
        usuarioId = usuarioId,
        materiaId = materiaId,
        profesorId = profesorId,
        dia = dia,
        horaInicio = horaInicio,
        horaFin = horaFin,
        color = color
    )
}


