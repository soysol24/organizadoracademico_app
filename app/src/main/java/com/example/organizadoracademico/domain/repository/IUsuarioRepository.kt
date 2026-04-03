package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface IUsuarioRepository {
    suspend fun getUsuarioById(id: Int): Usuario?
    suspend fun getUsuarioByEmail(email: String): Usuario?
    suspend fun login(email: String, password: String): Usuario?
    suspend fun register(nombre: String, email: String, password: String, fotoPerfil: String? = null): Usuario?
    suspend fun logout()
    suspend fun insertUsuario(usuario: Usuario)
    suspend fun updateUsuario(usuario: Usuario)
}