package com.example.organizadoracademico.data.sync

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.entities.HorarioEntity
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.dto.CreateHorarioRequestDto
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
    private val sessionManager: SessionManager = koin.get()
    private val imagenDao: ImagenDao = koin.get()
    private val horarioDao: HorarioDao = koin.get()
    private val materiaDao: MateriaDao = koin.get()
    private val profesorDao: ProfesorDao = koin.get()
    private val syncQueueDao: SyncQueueDao = koin.get()
    private val gson = Gson()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (!sessionManager.hasRemoteSession()) {
            Log.d(TAG, "doWork: sin sesion remota valida, se omite")
            return@withContext Result.success()
        }

        val items = syncQueueDao.getPending(limit = 40)
        Log.d(TAG, "doWork: pendientes=${items.size}")
        if (items.isEmpty()) return@withContext Result.success()

        var shouldRetry = false

        for (item in items) {
            Log.d(TAG, "doWork: procesando id=${item.id} entity=${item.entityType} action=${item.action} localId=${item.entityLocalId}")
            val success = try {
                processItem(item)
            } catch (e: Exception) {
                Log.e(TAG, "doWork: exception itemId=${item.id}", e)
                false
            }

            if (success) {
                syncQueueDao.deleteById(item.id)
                Log.d(TAG, "doWork: ok itemId=${item.id}")
            } else {
                syncQueueDao.incrementRetry(item.id)
                Log.w(TAG, "doWork: retry itemId=${item.id}")
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
                val horarioIdPart = resolveRemoteHorarioIdPart(imagen.horarioId)
                if (imagen.horarioId != null && horarioIdPart == null) {
                    Log.w(TAG, "processImagen.CREATE: horarioId local sin remoteId localHorarioId=${imagen.horarioId}")
                    return false
                }

                val response = apiService.uploadImagen(
                    file = filePart,
                    materiaId = materiaIdPart,
                    nota = notaPart,
                    horarioId = horarioIdPart
                )

                if (!response.isSuccessful) return false
                val dto = response.body() ?: return false

                imagenDao.insert(
                    imagen.copy(
                        remoteId = dto.id,
                        horarioId = dto.horarioId ?: imagen.horarioId,
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

                val request = mapHorarioRequestToRemoteIds(horario)
                if (request == null) {
                    Log.w(TAG, "processHorario.CREATE: mapeo remoto nulo localId=${item.entityLocalId}")
                    return false
                }
                val response = apiService.createHorario(request)
                Log.d(TAG, "processHorario.CREATE: http=${response.code()} ok=${response.isSuccessful} localId=${item.entityLocalId}")
                if (!response.isSuccessful) {
                    Log.w(TAG, "processHorario.CREATE: bodyError=${response.errorBody()?.string()}")
                    return false
                }
                val dto = response.body() ?: return false

                horarioDao.insert(horario.copy(remoteId = dto.id))
                Log.d(TAG, "processHorario.CREATE: remoteId=${dto.id} localId=${item.entityLocalId}")
                true
            }

            SyncAction.UPDATE -> {
                val horario = horarioDao.getById(item.entityLocalId) ?: return true
                val remoteId = horario.remoteId ?: return true

                val request = mapHorarioRequestToRemoteIds(horario)
                if (request == null) {
                    Log.w(TAG, "processHorario.UPDATE: mapeo remoto nulo localId=${item.entityLocalId}")
                    return false
                }
                val response = apiService.updateHorario(remoteId, request)
                val isGone = response.code() == 404
                Log.d(TAG, "processHorario.UPDATE: http=${response.code()} ok=${response.isSuccessful} remoteId=$remoteId")
                if (!response.isSuccessful && !isGone) {
                    Log.w(TAG, "processHorario.UPDATE: bodyError=${response.errorBody()?.string()}")
                }
                response.isSuccessful || isGone
            }

            SyncAction.DELETE -> {
                val payload = item.payload?.let { gson.fromJson(it, HorarioDeletePayload::class.java) } ?: return true
                val response = apiService.deleteHorario(payload.remoteId)
                val isGone = response.code() == 404
                Log.d(TAG, "processHorario.DELETE: http=${response.code()} ok=${response.isSuccessful} remoteId=${payload.remoteId}")
                if (!response.isSuccessful && !isGone) {
                    Log.w(TAG, "processHorario.DELETE: bodyError=${response.errorBody()?.string()}")
                }
                // DELETE idempotente: 404 significa que ya no existe para el usuario.
                response.isSuccessful || isGone
            }

            else -> true
        }
    }

    private suspend fun mapHorarioRequestToRemoteIds(horario: HorarioEntity): CreateHorarioRequestDto? {
        val baseRequest = horario.toDomain().toCreateRequestDto()
        
        // Los IDs locales de materias y profesores ahora coinciden directamente con los IDs remotos
        // después del cambio a ID-driven sync (DataInitializer asigna ids 1..54 en orden)
        Log.d(
            TAG,
            "mapHorarioRequestToRemoteIds: localMateria=${horario.materiaId} localProfesor=${horario.profesorId} (usando directamente como remote IDs)"
        )

        // Simplemente usar los IDs locales directamente como IDs remotos
        return baseRequest.copy(
            materiaId = horario.materiaId,
            profesorId = horario.profesorId
        )
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

    private suspend fun resolveRemoteHorarioIdPart(localHorarioId: Int?): okhttp3.RequestBody? {
        if (localHorarioId == null) return null
        val horario = horarioDao.getById(localHorarioId) ?: return null
        val remoteHorarioId = horario.remoteId ?: return null
        return remoteHorarioId.toString().toRequestBody("text/plain".toMediaType())
    }

    companion object {
        private const val TAG = "SYNC_HORARIOS"
    }
}

