package com.example.organizadoracademico.domain.usercase.usuario

import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class RegistroUseCase(
    private val repository: IUsuarioRepository
) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<Usuario> {
        return try {
            // Verificar si el email ya existe
            val usuarioExistente = repository.getUsuarioByEmail(email)
            if (usuarioExistente != null) {
                return Result.failure(Exception("El email ya está registrado"))
            }

            // IMPORTANTE: Aquí se debería hashear la contraseña antes de guardarla.
            val nuevoUsuario = Usuario(nombre = nombre, email = email, password = password)

            repository.insertUsuario(nuevoUsuario)

            // Recuperar usuario con email para obtener el objeto completo con ID
            val usuarioCreado = repository.getUsuarioByEmail(email)
            if (usuarioCreado != null) {
                Result.success(usuarioCreado)
            } else {
                Result.failure(Exception("Error al crear usuario tras el registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
