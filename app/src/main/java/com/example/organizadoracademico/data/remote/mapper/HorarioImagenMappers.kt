package com.example.organizadoracademico.data.remote.mapper

import com.example.organizadoracademico.data.remote.dto.CreateHorarioRequestDto
import com.example.organizadoracademico.data.remote.dto.HorarioDto
import com.example.organizadoracademico.data.remote.dto.ImagenDto
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.model.Imagen
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
    color = color,
    startAt = buildStartAtIsoUtc(dia = dia, hora = horaInicio)
)

fun ImagenDto.toDomain(): Imagen = Imagen(
    id = id,
    materiaId = materiaId,
    usuarioId = usuarioId,
    horarioId = horarioId,
    uri = uri,
    nota = nota,
    fecha = fecha,
    favorita = favorita
)

private fun buildStartAtIsoUtc(dia: String, hora: String): String? {
    val targetDay = mapDiaToCalendar(dia) ?: return null
    val (hour, minute) = parseHourMinute(hora) ?: return null

    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }

    // Buscamos la próxima ocurrencia de ese día+hora (hoy o dentro de la semana).
    repeat(8) {
        if (target.get(Calendar.DAY_OF_WEEK) == targetDay && !target.before(now)) {
            return toIsoUtc(target)
        }
        target.add(Calendar.DATE, 1)
    }

    return null
}

private fun parseHourMinute(hora: String): Pair<Int, Int>? {
    val parts = hora.split(":")
    if (parts.size != 2) return null

    val h = parts[0].trim().toIntOrNull() ?: return null
    val m = parts[1].trim().toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null

    return h to m
}

private fun mapDiaToCalendar(dia: String): Int? {
    val normalized = Normalizer.normalize(dia.trim(), Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .lowercase(Locale.US)

    return when (normalized) {
        "domingo" -> Calendar.SUNDAY
        "lunes" -> Calendar.MONDAY
        "martes" -> Calendar.TUESDAY
        "miercoles" -> Calendar.WEDNESDAY
        "jueves" -> Calendar.THURSDAY
        "viernes" -> Calendar.FRIDAY
        "sabado" -> Calendar.SATURDAY
        else -> null
    }
}

private fun toIsoUtc(calendar: Calendar): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(calendar.time)
}
