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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(horario: HorarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReturnId(horario: HorarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(horarios: List<HorarioEntity>)

    @Query("DELETE FROM horarios WHERE usuarioId = :userId")
    suspend fun deleteAllByUsuario(userId: Int)

    @Query("DELETE FROM horarios WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM horarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): HorarioEntity?

    @Query("SELECT * FROM horarios WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): HorarioEntity?
}