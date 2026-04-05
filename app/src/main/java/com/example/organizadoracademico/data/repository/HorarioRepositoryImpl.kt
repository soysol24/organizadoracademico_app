package com.example.organizadoracademico.data.repository

import android.util.Log
import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.mapper.toDomain as remoteToDomain
import com.example.organizadoracademico.data.sync.HorarioDeletePayload
import com.example.organizadoracademico.data.sync.SyncAction
import com.example.organizadoracademico.data.sync.SyncEntityType
import com.example.organizadoracademico.data.sync.SyncScheduler
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.repository.IHorarioRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.organizadoracademico.data.local.util.SessionManager

class HorarioRepositoryImpl(
    private val dao: HorarioDao,
    private val apiService: ApiService,
    private val materiaDao: MateriaDao,
    private val profesorDao: ProfesorDao,
    private val syncQueueDao: SyncQueueDao,
    private val syncScheduler: SyncScheduler,
    private val sessionManager: SessionManager
) : IHorarioRepository {

    private val gson = Gson()

    // Ahora pide el userId para filtrar
    override fun getAllHorarios(userId: Int): Flow<List<Horario>> {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "getAllHorarios: inicia sync userId=$userId")
            syncScheduler.scheduleNow()
            syncHorarios(userId)
        }

        return dao.getAllByUsuario(userId).map { entities ->
            Log.d(TAG, "getAllHorarios: locales=${entities.size} userId=$userId")
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertHorario(horario: Horario) {
        val localId = dao.insertAndReturnId(horario.toEntity()).toInt()
        Log.d(TAG, "insertHorario: localId=$localId materia=${horario.materiaId} profesor=${horario.profesorId}")
        syncQueueDao.insert(
            SyncQueueEntity(
                entityType = SyncEntityType.HORARIO,
                action = SyncAction.CREATE,
                entityLocalId = localId
            )
        )
        syncScheduler.scheduleNow()
    }

    override suspend fun deleteHorario(id: Int) {
        val horario = dao.getById(id) ?: return
        Log.d(TAG, "deleteHorario: localId=$id remoteId=${horario.remoteId}")

        // Limpiamos operaciones previas para esta entidad y reconstruimos intención actual.
        syncQueueDao.deleteByEntity(SyncEntityType.HORARIO, id)

        val remoteId = horario.remoteId
        if (remoteId == null) {
            // Nunca subido al servidor: borrado local inmediato.
            dao.deleteById(id)
            syncScheduler.scheduleNow()
            return
        }

        // Si hay sesión remota válida, intentamos borrar en backend en el momento.
        val immediateDelete = if (sessionManager.hasRemoteSession()) {
            runCatching { apiService.deleteHorario(remoteId) }.getOrNull()
        } else {
            null
        }

        if (immediateDelete != null) {
            val isDeletedRemotely = immediateDelete.isSuccessful || immediateDelete.code() == 404
            Log.d(TAG, "deleteHorario: immediate http=${immediateDelete.code()} ok=${immediateDelete.isSuccessful}")
            if (isDeletedRemotely) {
                dao.deleteById(id)
                return
            }
            Log.w(TAG, "deleteHorario: immediate bodyError=${immediateDelete.errorBody()?.string()}")
        }

        // Fallback offline/reintento: encolamos DELETE, pero mantenemos local hasta confirmación remota.
        syncQueueDao.insert(
            SyncQueueEntity(
                entityType = SyncEntityType.HORARIO,
                action = SyncAction.DELETE,
                entityLocalId = id,
                payload = gson.toJson(HorarioDeletePayload(remoteId = remoteId))
            )
        )
        syncScheduler.scheduleNow()
    }

    override suspend fun updateHorario(horario: Horario) {
        val existing = dao.getById(horario.id) ?: return
        val updated = horario.toEntity().copy(remoteId = existing.remoteId)
        dao.insert(updated)

        if (existing.remoteId != null) {
            // Consolidamos posibles reintentos de update del mismo horario.
            syncQueueDao.deleteByEntity(SyncEntityType.HORARIO, horario.id)
            syncQueueDao.insert(
                SyncQueueEntity(
                    entityType = SyncEntityType.HORARIO,
                    action = SyncAction.UPDATE,
                    entityLocalId = horario.id
                )
            )
        }

        syncScheduler.scheduleNow()
    }

    private suspend fun syncHorarios(userId: Int) {
        try {
            // Evita errores FK al insertar horarios remotos cuando aún no existe catálogo local.
            syncCatalogosBase()

            val response = apiService.getHorarios()
            Log.d(TAG, "syncHorarios: http=${response.code()} ok=${response.isSuccessful}")
            if (!response.isSuccessful) {
                Log.w(TAG, "syncHorarios: bodyError=${response.errorBody()?.string()}")
                return
            }

            val pendingItems = syncQueueDao.getPendingByEntityType(SyncEntityType.HORARIO)
            val pendingUpdateLocalIds = pendingItems
                .filter { it.action == SyncAction.UPDATE }
                .map { it.entityLocalId }
                .toSet()

            Log.d(
                TAG,
                "syncHorarios: pending=${pendingItems.size} pendingUpdates=${pendingUpdateLocalIds.size}"
            )

            val remoteItems = response.body().orEmpty()
            Log.d(TAG, "syncHorarios: remotos=${remoteItems.size}")

            remoteItems
                // El backend ya filtra por el usuario del token; no dependemos del userId local remoto.
                .forEach { dto ->
                    val existing = dao.getByRemoteId(dto.id)
                    if (existing != null && existing.id in pendingUpdateLocalIds) {
                        Log.d(TAG, "syncHorarios: skip remoteId=${dto.id} por update pendiente localId=${existing.id}")
                        return@forEach
                    }

                    val merged = if (existing != null) {
                        existing.copy(
                            usuarioId = userId,
                            materiaId = dto.materiaId,
                            profesorId = dto.profesorId,
                            dia = dto.dia,
                            horaInicio = dto.horaInicio,
                            horaFin = dto.horaFin,
                            color = dto.color,
                            remoteId = dto.id
                        )
                    } else {
                        dto.remoteToDomain()
                            .toEntity()
                            .copy(id = 0, usuarioId = userId, remoteId = dto.id)
                    }
                    try {
                        dao.insert(merged)
                        Log.d(TAG, "syncHorarios: upsert remoteId=${dto.id} localId=${merged.id}")
                    } catch (e: Exception) {
                        Log.e(TAG, "syncHorarios: fallo insert remoteId=${dto.id}", e)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "syncHorarios: exception", e)
        }
    }

    private suspend fun syncCatalogosBase() {
        runCatching {
            val materiasResponse = apiService.getMaterias()
            Log.d(TAG, "syncCatalogosBase.materias: http=${materiasResponse.code()} ok=${materiasResponse.isSuccessful}")
            val materias = materiasResponse.body().orEmpty()
            materias.forEach { dto ->
                materiaDao.insert(
                    MateriaEntity(
                        id = dto.id,
                        nombre = dto.nombre,
                        color = dto.color,
                        icono = dto.icono
                    )
                )
            }
            Log.d(TAG, "syncCatalogosBase.materias: upsert=${materias.size}")
        }.onFailure { e ->
            Log.e(TAG, "syncCatalogosBase.materias: exception", e)
        }

        runCatching {
            val profesoresResponse = apiService.getProfesores()
            Log.d(TAG, "syncCatalogosBase.profesores: http=${profesoresResponse.code()} ok=${profesoresResponse.isSuccessful}")
            val profesores = profesoresResponse.body().orEmpty()
            profesores.forEach { dto ->
                profesorDao.insert(ProfesorEntity(id = dto.id, nombre = dto.nombre))
            }
            Log.d(TAG, "syncCatalogosBase.profesores: upsert=${profesores.size}")
        }.onFailure { e ->
            Log.e(TAG, "syncCatalogosBase.profesores: exception", e)
        }
    }

    companion object {
        private const val TAG = "SYNC_HORARIOS"
    }
}