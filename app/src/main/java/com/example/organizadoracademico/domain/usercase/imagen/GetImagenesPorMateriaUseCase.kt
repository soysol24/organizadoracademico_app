package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository
import kotlinx.coroutines.flow.Flow

class GetImagenesPorMateriaUseCase(
    private val repository: IImagenRepository
) {
    operator fun invoke(materiaId: Int): Flow<List<Imagen>> {
        return repository.getImagenesByMateria(materiaId)
    }
}