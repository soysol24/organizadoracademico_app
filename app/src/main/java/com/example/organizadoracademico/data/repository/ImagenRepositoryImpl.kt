package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ImagenFirestoreService
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ImagenRepositoryImpl(
    private val dao: ImagenDao,
    private val remoteService: ImagenFirestoreService
) : IImagenRepository {

    // 1. Filtramos localmente por materia Y usuario
    override fun getImagenesByMateria(materiaId: Int, userId: Int): Flow<List<Imagen>> =
        dao.getByMateria(materiaId, userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getImagenById(id: Int): Imagen? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertImagen(imagen: Imagen) {
        // 2. Guardamos en Room (Local)
        // Es vital que imagen.usuarioId no sea 0 aquí
        dao.insert(imagen.toEntity())

        // 3. Sincronizamos con la nube (Remoto)
        try {
            remoteService.save(imagen)
        } catch (e: Exception) {
            // Si falla el internet, la foto sigue en Room.
            // Podrías implementar una lógica de reintento luego.
            e.printStackTrace()
        }
    }

    override suspend fun updateNota(id: Int, nota: String) {
        dao.updateNota(id, nota)
        // TODO: Actualizar también en remoto si es necesario
    }

    override suspend fun toggleFavorita(id: Int, favorita: Boolean) {
        dao.toggleFavorita(id, favorita)
    }

    override suspend fun deleteImagen(id: Int) {
        dao.deleteById(id)
        try {
            remoteService.delete(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}