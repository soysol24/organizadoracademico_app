package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.repository.IImagenRepository

class SaveImagenConNotaUseCase(
    private val repository: IImagenRepository
) {
    suspend operator fun invoke(
        materiaId: Int,
        usuarioId: Int, // <-- Nuevo parámetro
        uri: String,
        nota: String? = null
    ): Result<Unit> {
        return try {
            val imagen = Imagen(
                materiaId = materiaId,
                usuarioId = usuarioId, // <-- ASIGNACIÓN CRUCIAL
                uri = uri,
                nota = nota,
                fecha = System.currentTimeMillis()
            )
            repository.insertImagen(imagen)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}