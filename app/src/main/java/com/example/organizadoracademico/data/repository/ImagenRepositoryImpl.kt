package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImagenRepositoryImpl(
    private val dao: ImagenDao
) : IImagenRepository {

    override fun getImagenesByMateria(materiaId: Int): Flow<List<Imagen>> =
        dao.getByMateria(materiaId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getImagenById(id: Int): Imagen? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertImagen(imagen: Imagen) {
        dao.insert(imagen.toEntity())
    }

    override suspend fun updateNota(id: Int, nota: String) {
        dao.updateNota(id, nota)
    }

    override suspend fun toggleFavorita(id: Int, favorita: Boolean) {
        dao.toggleFavorita(id, favorita)
    }

    override suspend fun deleteImagen(id: Int) {
        dao.deleteById(id)
    }
}