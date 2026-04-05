package com.example.organizadoracademico.domain.usercase.usuario

import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class RegistroUseCase(
    private val repository: IUsuarioRepository
) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<Usuario> {
        return try {
            val usuarioCreado = repository.register(
                nombre = nombre,
                email = email,
                password = password
            )
            if (usuarioCreado != null) {
                Result.success(usuarioCreado)
            } else {
                Result.failure(Exception("No se pudo registrar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
