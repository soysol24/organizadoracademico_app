package com.example.organizadoracademico.domain.model

data class Horario(
    val id: Int = 0,
    val materiaId: Int,
    val profesorId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val color: String
)