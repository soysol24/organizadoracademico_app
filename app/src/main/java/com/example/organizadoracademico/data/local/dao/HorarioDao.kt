package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.organizadoracademico.data.local.entities.HorarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horarios WHERE usuarioId = :userId ORDER BY dia, horaInicio ASC")
    fun getAllByUsuario(userId: Int): Flow<List<HorarioEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(horario: HorarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAndReturnId(horario: HorarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAndReturnIdOrThrow(horario: HorarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(horarios: List<HorarioEntity>): List<Long>

    @Query("DELETE FROM horarios WHERE usuarioId = :userId")
    suspend fun deleteAllByUsuario(userId: Int)

    @Query("DELETE FROM horarios WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM horarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): HorarioEntity?

    @Query("SELECT * FROM horarios WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): HorarioEntity?

    // Verifica si existe un horario duplicado (mismo usuario, día, hora inicio y hora fin)
    @Query(
        "SELECT COUNT(*) FROM horarios " +
        "WHERE usuarioId = :usuarioId AND dia = :dia AND horaInicio = :horaInicio AND horaFin = :horaFin"
    )
    suspend fun existeDuplicado(
        usuarioId: Int,
        dia: String,
        horaInicio: String,
        horaFin: String
    ): Int

    @Query(
        "SELECT COUNT(*) FROM horarios " +
            "WHERE usuarioId = :usuarioId " +
            "AND dia = :dia " +
            "AND time(horaInicio) < time(:horaFin) " +
            "AND time(horaFin) > time(:horaInicio)"
    )
    suspend fun countTraslapes(
        usuarioId: Int,
        dia: String,
        horaInicio: String,
        horaFin: String
    ): Int
}