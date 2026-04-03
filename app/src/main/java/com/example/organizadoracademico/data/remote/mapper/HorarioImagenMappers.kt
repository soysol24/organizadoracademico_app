package com.example.organizadoracademico.data.remote.mapper

import com.example.organizadoracademico.data.remote.dto.CreateHorarioRequestDto
import com.example.organizadoracademico.data.remote.dto.HorarioDto
import com.example.organizadoracademico.data.remote.dto.ImagenDto
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.model.Imagen

fun HorarioDto.toDomain(): Horario = Horario(
    id = id,
    usuarioId = usuarioId,
    materiaId = materiaId,
    profesorId = profesorId,
    dia = dia,
    horaInicio = horaInicio,
    horaFin = horaFin,
    color = color
)

fun Horario.toCreateRequestDto(): CreateHorarioRequestDto = CreateHorarioRequestDto(
    materiaId = materiaId,
    profesorId = profesorId,
    dia = dia,
    horaInicio = horaInicio,
    horaFin = horaFin,
    color = color
)

fun ImagenDto.toDomain(): Imagen = Imagen(
    id = id,
    materiaId = materiaId,
    usuarioId = usuarioId,
    uri = uri,
    nota = nota,
    fecha = fecha,
    favorita = favorita
)
