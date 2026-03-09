package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Horario

@Entity(
    tableName = "horarios",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["materiaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProfesorEntity::class,
            parentColumns = ["id"],
            childColumns = ["profesorId"],
            onDelete = ForeignKey.CASCADE
        ),
        // --- NUEVA LLAVE FORÁNEA ---
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HorarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int, // <-- AÑADIDO: Ahora el DAO podrá encontrarlo
    val materiaId: Int,
    val profesorId: Int,
    val dia: String,
    val horaInicio: String,
    val horaFin: String,
    val color: String
)

// Mapeadores corregidos
fun HorarioEntity.toDomain(): Horario = Horario(
    id, usuarioId, materiaId, profesorId, dia, horaInicio, horaFin, color
)
fun Horario.toEntity(): HorarioEntity = HorarioEntity(
    id, usuarioId, materiaId, profesorId, dia, horaInicio, horaFin, color
)