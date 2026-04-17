package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    fun getAll(): Flow<List<MateriaEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(materia: MateriaEntity)

    @Update
    suspend fun update(materia: MateriaEntity)

    @Query("SELECT * FROM materias WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): MateriaEntity?

    @Query("SELECT * FROM materias WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): MateriaEntity?

    @Query("DELETE FROM materias WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM materias")
    suspend fun getAllStatic(): List<MateriaEntity>

    // CAMBIO: Renombrado a getCountGlobal para que coincida con el DataInitializer
    @Query("SELECT COUNT(*) FROM materias")
    suspend fun getCountGlobal(): Int
}