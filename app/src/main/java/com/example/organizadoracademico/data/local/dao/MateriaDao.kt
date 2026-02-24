package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    fun getAll(): Flow<List<MateriaEntity>>

    @Insert
    suspend fun insert(materia: MateriaEntity)

    @Query("DELETE FROM materias WHERE id = :id")
    suspend fun deleteById(id: Int)
}