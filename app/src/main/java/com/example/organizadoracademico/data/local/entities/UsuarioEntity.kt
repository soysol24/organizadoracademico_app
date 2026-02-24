package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Usuario

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String, // Campo de contraseña
    val fotoPerfil: String? = null
)

fun UsuarioEntity.toDomain(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    email = email,
    password = password, // Mapear contraseña
    fotoPerfil = fotoPerfil
)

fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nombre = nombre,
    email = email,
    password = password, // Mapear contraseña
    fotoPerfil = fotoPerfil
)