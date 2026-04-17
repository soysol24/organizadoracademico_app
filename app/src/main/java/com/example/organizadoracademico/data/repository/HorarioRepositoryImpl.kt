package com.example.organizadoracademico.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.dao.UsuarioDao
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.local.entities.UsuarioEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.mapper.toDomain as remoteToDomain
import com.example.organizadoracademico.data.sync.HorarioDeletePayload
import com.example.organizadoracademico.data.sync.SyncAction
import com.example.organizadoracademico.data.sync.SyncEntityType
import com.example.organizadoracademico.data.sync.SyncScheduler
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.exception.HorarioDuplicadoException
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
    private val usuarioDao: UsuarioDao,
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
        ensureLocalReferencesForInsert(horario)
        val localId = try {
            dao.insertAndReturnIdOrThrow(horario.toEntity()).toInt()
        } catch (e: Exception) {
            if (isDuplicateHorarioConstraint(e)) {
                throw HorarioDuplicadoException()
            }
            throw e
        }
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

    private suspend fun ensureLocalReferencesForInsert(horario: Horario) {
        val user = usuarioDao.getById(horario.usuarioId)
        if (user == null) {
            val sessionName = sessionManager.getUserName().orEmpty().ifBlank { "Usuario" }
            val placeholderEmail = "local_${horario.usuarioId}@placeholder.local"
            usuarioDao.insert(
                UsuarioEntity(
                    id = horario.usuarioId,
                    nombre = sessionName,
                    email = placeholderEmail,
                    password = ""
                )
            )
            Log.w(TAG, "insertHorario: usuario local faltante, se creo placeholder userId=${horario.usuarioId}")
        }

        val materia = materiaDao.getById(horario.materiaId)
        requireNotNull(materia) { "Materia local no encontrada id=${horario.materiaId}" }

        val profesor = profesorDao.getById(horario.profesorId)
        requireNotNull(profesor) { "Profesor local no encontrado id=${horario.profesorId}" }
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

    override suspend fun existeTraslapeHorario(
        usuarioId: Int,
        dia: String,
        horaInicio: String,
        horaFin: String
    ): Boolean {
        return dao.countTraslapes(
            usuarioId = usuarioId,
            dia = dia,
            horaInicio = horaInicio,
            horaFin = horaFin
        ) > 0
    }

    private suspend fun syncHorarios(userId: Int) {
        try {
            // Evita errores FK al insertar horarios remotos cuando aún no existe catálogo local.
            val (materiaIdMap, profesorIdMap) = syncCatalogosBase()

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
                    val localMateriaId = materiaIdMap[dto.materiaId]
                    val localProfesorId = profesorIdMap[dto.profesorId]
                    if (localMateriaId == null || localProfesorId == null) {
                        Log.w(
                            TAG,
                            "syncHorarios: sin mapeo local remoteHorario=${dto.id} remoteMateria=${dto.materiaId} remoteProfesor=${dto.profesorId}"
                        )
                        return@forEach
                    }

                    val existing = dao.getByRemoteId(dto.id)
                    if (existing != null && existing.id in pendingUpdateLocalIds) {
                        Log.d(TAG, "syncHorarios: skip remoteId=${dto.id} por update pendiente localId=${existing.id}")
                        return@forEach
                    }

                    val merged = if (existing != null) {
                        existing.copy(
                            usuarioId = userId,
                            materiaId = localMateriaId,
                            profesorId = localProfesorId,
                            dia = dto.dia,
                            horaInicio = dto.horaInicio,
                            horaFin = dto.horaFin,
                            color = dto.color,
                            remoteId = dto.id
                        )
                    } else {
                        dto.remoteToDomain()
                            .toEntity()
                            .copy(
                                id = 0,
                                usuarioId = userId,
                                materiaId = localMateriaId,
                                profesorId = localProfesorId,
                                remoteId = dto.id
                            )
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

    private suspend fun syncCatalogosBase(): Pair<Map<Int, Int>, Map<Int, Int>> {
        val materiaIdMap = mutableMapOf<Int, Int>()
        val profesorIdMap = mutableMapOf<Int, Int>()

        runCatching {
            val materiasResponse = apiService.getMaterias()
            Log.d(TAG, "syncCatalogosBase.materias: http=${materiasResponse.code()} ok=${materiasResponse.isSuccessful}")
            val materias = materiasResponse.body().orEmpty()
            materias.forEach { dto ->
                val existing = materiaDao.getById(dto.id)
                if (existing == null) {
                    materiaDao.insert(
                        MateriaEntity(id = dto.id, nombre = dto.nombre, color = dto.color, icono = dto.icono)
                    )
                } else if (
                    existing.nombre != dto.nombre ||
                    existing.color != dto.color ||
                    existing.icono != dto.icono
                ) {
                    materiaDao.update(
                        existing.copy(
                            nombre = dto.nombre,
                            color = dto.color,
                            icono = dto.icono
                        )
                    )
                }
                materiaIdMap[dto.id] = dto.id
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
                val existing = profesorDao.getByNombre(dto.nombre)
                if (existing == null) {
                    profesorDao.insert(ProfesorEntity(id = 0, nombre = dto.nombre))
                }
                profesorDao.getByNombre(dto.nombre)?.id?.let { localId ->
                    profesorIdMap[dto.id] = localId
                }
            }
            Log.d(TAG, "syncCatalogosBase.profesores: upsert=${profesores.size}")
        }.onFailure { e ->
            Log.e(TAG, "syncCatalogosBase.profesores: exception", e)
        }

        return materiaIdMap to profesorIdMap
    }

    companion object {
        private const val TAG = "SYNC_HORARIOS"
    }

    private fun isDuplicateHorarioConstraint(error: Throwable): Boolean {
        if (error is SQLiteConstraintException) return true

        val fullMessage = buildString {
            append(error.message.orEmpty())
            val causeMessage = error.cause?.message.orEmpty()
            if (causeMessage.isNotBlank()) {
                append(" ")
                append(causeMessage)
            }
        }.lowercase()

        return fullMessage.contains("unique constraint failed") && fullMessage.contains("horarios")
    }
}