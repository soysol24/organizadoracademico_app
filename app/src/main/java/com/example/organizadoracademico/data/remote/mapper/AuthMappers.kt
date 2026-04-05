package com.example.organizadoracademico.data.remote.mapper

import com.example.organizadoracademico.data.remote.dto.AuthUserDto
import com.example.organizadoracademico.domain.model.Usuario

fun AuthUserDto.toDomain(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    email = email,
    password = password,
    fotoPerfil = fotoPerfil
)

