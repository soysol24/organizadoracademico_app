package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository

class GetImagenUseCase(private val repository: IImagenRepository) {
    suspend operator fun invoke(id: Int): Imagen? {
        return repository.getImagenById(id)
    }
}