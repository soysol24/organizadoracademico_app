package com.example.organizadoracademico.data.remote.dto

data class MateriaDto(
    val id: Int,
    val nombre: String,
    val color: String,
    val icono: String?
)

data class ProfesorDto(
    val id: Int,
    val nombre: String
)

