package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesorDao {
    @Query("SELECT * FROM profesores ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProfesorEntity>>

    @Query("SELECT COUNT(*) FROM profesores")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profesor: ProfesorEntity)

    @Update
    suspend fun update(profesor: ProfesorEntity)

    @Query("SELECT * FROM profesores WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ProfesorEntity?

    @Query("SELECT * FROM profesores WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): ProfesorEntity?
}