package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.organizadoracademico.data.local.entities.ImagenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImagenDao {
    // 1. Filtramos por materia Y por usuario para máxima seguridad
    @Query("SELECT * FROM imagenes WHERE materiaId = :materiaId AND usuarioId = :userId ORDER BY fecha DESC")
    fun getByMateria(materiaId: Int, userId: Int): Flow<List<ImagenEntity>>

    // 2. Nueva función: ¿Quieres ver TODAS las fotos de un usuario sin importar la materia?
    @Query("SELECT * FROM imagenes WHERE usuarioId = :userId ORDER BY fecha DESC")
    fun getAllByUsuario(userId: Int): Flow<List<ImagenEntity>>

    @Query("SELECT * FROM imagenes WHERE id = :id")
    suspend fun getById(id: Int): ImagenEntity?

    @Insert
    suspend fun insert(imagen: ImagenEntity)

    @Update
    suspend fun update(imagen: ImagenEntity)

    @Query("UPDATE imagenes SET nota = :nota WHERE id = :id")
    suspend fun updateNota(id: Int, nota: String)

    @Query("UPDATE imagenes SET favorita = :favorita WHERE id = :id")
    suspend fun toggleFavorita(id: Int, favorita: Boolean)

    @Query("DELETE FROM imagenes WHERE id = :id")
    suspend fun deleteById(id: Int)
}