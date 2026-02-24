package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.UsuarioDao
import com.example.organizadoracademico.data.local.entities.UsuarioEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao
) : IUsuarioRepository {

    override suspend fun getUsuarioById(id: Int): Usuario? {
        return dao.getById(id)?.toDomain()
    }
    override suspend fun getUsuarioByEmail(email: String): Usuario? {  // <-- NUEVO
        return dao.getByEmail(email)?.toDomain()
    }

    override suspend fun login(email: String, password: String): Usuario? {
        // Por ahora simplificado, luego implementaremos con contraseña real
        return dao.getByEmail(email)?.toDomain()
    }

    override suspend fun insertUsuario(usuario: Usuario) {  // <-- Cambiado
        dao.insert(usuario.toEntity())
    }

    override suspend fun updateUsuario(usuario: Usuario) {
        dao.update(usuario.toEntity())
    }
}