package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.UsuarioDao
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.dto.LoginRequestDto
import com.example.organizadoracademico.data.remote.dto.RegisterRequestDto
import com.example.organizadoracademico.data.remote.mapper.toDomain
import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : IUsuarioRepository {

    override suspend fun getUsuarioById(id: Int): Usuario? {
        return dao.getById(id)?.toDomain()
    }
    override suspend fun getUsuarioByEmail(email: String): Usuario? {
        return dao.getByEmail(email)?.toDomain()
    }

    override suspend fun login(email: String, password: String): Usuario? {
        return try {
            val response = apiService.login(LoginRequestDto(email = email, password = password))
            if (!response.isSuccessful) return null

            val body = response.body() ?: return null
            val usuario = body.user.toDomain()
            sessionManager.saveSession(usuario.id, usuario.nombre, body.token)
            dao.insert(usuario.toEntity())
            usuario
        } catch (_: Exception) {
            dao.getByEmail(email)?.toDomain()?.takeIf { it.password == password }?.also {
                sessionManager.saveSession(it.id, it.nombre)
            }
        }
    }

    override suspend fun register(
        nombre: String,
        email: String,
        password: String,
        fotoPerfil: String?
    ): Usuario? {
        val response = apiService.register(
            RegisterRequestDto(
                nombre = nombre,
                email = email,
                password = password,
                fotoPerfil = fotoPerfil
            )
        )

        if (!response.isSuccessful) return null

        val body = response.body() ?: return null
        val usuario = body.user.toDomain()
        sessionManager.saveSession(usuario.id, usuario.nombre, body.token)
        dao.insert(usuario.toEntity())
        return usuario
    }

    override suspend fun logout() {
        sessionManager.logout()
    }

    override suspend fun insertUsuario(usuario: Usuario) {
        dao.insert(usuario.toEntity())
    }

    override suspend fun updateUsuario(usuario: Usuario) {
        dao.update(usuario.toEntity())
    }
}