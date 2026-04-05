package com.example.organizadoracademico.domain.model

data class Horario(
    val id: Int = 0,
    val usuarioId: Int,
    val materiaId: Int = 0,
    val profesorId: Int = 0,
    val dia: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val color: String = "",
    val pendienteSync: Boolean = false
)