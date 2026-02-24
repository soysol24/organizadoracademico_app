package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Imagen
import kotlinx.coroutines.flow.Flow

interface IImagenRepository {
    fun getImagenesByMateria(materiaId: Int): Flow<List<Imagen>>
    suspend fun getImagenById(id: Int): Imagen?
    suspend fun insertImagen(imagen: Imagen)
    suspend fun updateNota(id: Int, nota: String)
    suspend fun toggleFavorita(id: Int, favorita: Boolean)
    suspend fun deleteImagen(id: Int)
}