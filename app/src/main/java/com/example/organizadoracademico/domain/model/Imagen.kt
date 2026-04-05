package com.example.organizadoracademico.domain.model

data class Imagen(
    val id: Int = 0,
    val materiaId: Int = 0,
    val usuarioId: Int = 0,
    val horarioId: Int? = null,
    val uri: String = "",
    val nota: String? = null,
    val fecha: Long = System.currentTimeMillis(),
    val favorita: Boolean = false
)