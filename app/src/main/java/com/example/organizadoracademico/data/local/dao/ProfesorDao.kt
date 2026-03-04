package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesorDao {
    @Query("SELECT * FROM profesores ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProfesorEntity>>

    @Insert
    suspend fun insert(profesor: ProfesorEntity)

    @Query("SELECT * FROM profesores LIMIT 1") // O la tabla que corresponda
    suspend fun getAllStatic(): List<ProfesorEntity>

    @Query("SELECT COUNT(*) FROM profesores")
    suspend fun getCount(): Int

}