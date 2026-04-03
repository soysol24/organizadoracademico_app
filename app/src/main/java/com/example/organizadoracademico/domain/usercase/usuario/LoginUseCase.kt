package com.example.organizadoracademico.domain.usercase.usuario

import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class LoginUseCase(
    private val repository: IUsuarioRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Usuario> {
        return try {
            val usuario = repository.login(email, password)
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Email o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}