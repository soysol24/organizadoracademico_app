package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
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

class HorarioRepositoryImpl(
    private val dao: HorarioDao,
    private val apiService: ApiService,
    private val syncQueueDao: SyncQueueDao,
    private val syncScheduler: SyncScheduler
) : IHorarioRepository {

    private val gson = Gson()

    // Ahora pide el userId para filtrar
    override fun getAllHorarios(userId: Int): Flow<List<Horario>> {
        CoroutineScope(Dispatchers.IO).launch {
            syncHorarios(userId)
            syncScheduler.scheduleNow()
        }

        return dao.getAllByUsuario(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertHorario(horario: Horario) {
        val localId = dao.insertAndReturnId(horario.toEntity()).toInt()
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
        val horario = dao.getById(id)
        syncQueueDao.deleteByEntity(SyncEntityType.HORARIO, id)

        if (horario?.remoteId != null) {
            syncQueueDao.insert(
                SyncQueueEntity(
                    entityType = SyncEntityType.HORARIO,
                    action = SyncAction.DELETE,
                    entityLocalId = id,
                    payload = gson.toJson(HorarioDeletePayload(remoteId = horario.remoteId))
                )
            )
        }

        dao.deleteById(id)
        syncScheduler.scheduleNow()
    }

    private suspend fun syncHorarios(userId: Int) {
        try {
            val response = apiService.getHorarios()
            if (!response.isSuccessful) return

            response.body().orEmpty()
                .filter { it.usuarioId == userId }
                .forEach { dto ->
                    val existing = dao.getByRemoteId(dto.id)
                    val merged = if (existing != null) {
                        // Registro local ya vinculado al remoto: actualizar
                        existing.copy(
                            usuarioId = dto.usuarioId,
                            materiaId = dto.materiaId,
                            profesorId = dto.profesorId,
                            dia = dto.dia,
                            horaInicio = dto.horaInicio,
                            horaFin = dto.horaFin,
                            color = dto.color,
                            remoteId = dto.id
                        )
                    } else {
                        // Registro nuevo de la nube: id = 0 para que Room
                        // autogenere un ID local que NO pise registros pendientes
                        dto.remoteToDomain()
                            .toEntity()
                            .copy(id = 0, remoteId = dto.id)
                    }
                    try {
                        dao.insert(merged)
                    } catch (_: Exception) {
                        // FK constraint: materia/profesor aún no sincronizado localmente
                    }
                }
        } catch (_: Exception) {
        }
    }
}