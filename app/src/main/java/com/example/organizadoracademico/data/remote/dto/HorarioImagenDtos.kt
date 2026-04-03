package com.example.organizadoracademico.data.remote.dto

data class HorarioDto(
    val id: Int,
    val usuarioId: Int,
    val materiaId: Int,
    val profesorId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val color: String
)

data class CreateHorarioRequestDto(
    val materiaId: Int,
    val profesorId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val color: String
)

data class ImagenDto(
    val id: Int,
    val materiaId: Int,
    val usuarioId: Int,
    val uri: String,
    val nota: String?,
    val fecha: Long,
    val favorita: Boolean
)

data class DeleteResponseDto(
    val deleted: Boolean
)
