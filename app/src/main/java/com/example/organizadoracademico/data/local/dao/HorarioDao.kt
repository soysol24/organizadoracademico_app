package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.organizadoracademico.data.local.entities.HorarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horarios ORDER BY dia, horaInicio ASC")
    fun getAll(): Flow<List<HorarioEntity>>

    @Insert
    suspend fun insert(horario: HorarioEntity)

    @Query("DELETE FROM horarios WHERE id = :id")
    suspend fun deleteById(id: Int)
}