package com.example.organizadoracademico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organizadoracademico.data.local.entities.ImagenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImagenDao {

    @Query("SELECT * FROM imagenes WHERE materiaId = :materiaId AND usuarioId = :userId ORDER BY fecha DESC")
    fun getByMateria(materiaId: Int, userId: Int): Flow<List<ImagenEntity>>

    @Query("SELECT * FROM imagenes WHERE materiaId = :materiaId AND usuarioId = :userId ORDER BY fecha DESC")
    suspend fun getByMateriaOnce(materiaId: Int, userId: Int): List<ImagenEntity>


    @Query("SELECT * FROM imagenes WHERE usuarioId = :userId ORDER BY fecha DESC")
    fun getAllByUsuario(userId: Int): Flow<List<ImagenEntity>>

    @Query("SELECT * FROM imagenes WHERE id = :id")
    suspend fun getById(id: Int): ImagenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imagen: ImagenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReturnId(imagen: ImagenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(imagenes: List<ImagenEntity>)

    @Query("DELETE FROM imagenes WHERE usuarioId = :userId")
    suspend fun deleteAllByUsuario(userId: Int)

    @Query("DELETE FROM imagenes WHERE materiaId = :materiaId AND usuarioId = :userId")
    suspend fun deleteByMateriaAndUsuario(materiaId: Int, userId: Int)

    @Update
    suspend fun update(imagen: ImagenEntity)

    @Query("SELECT * FROM imagenes WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): ImagenEntity?

    @Query("UPDATE imagenes SET nota = :nota WHERE id = :id")
    suspend fun updateNota(id: Int, nota: String)

    @Query("UPDATE imagenes SET favorita = :favorita WHERE id = :id")
    suspend fun toggleFavorita(id: Int, favorita: Boolean)

    @Query("DELETE FROM imagenes WHERE id = :id")
    suspend fun deleteById(id: Int)
}