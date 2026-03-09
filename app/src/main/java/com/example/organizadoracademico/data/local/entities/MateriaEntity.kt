package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Materia

@Entity(tableName = "materias")
data class MateriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val color: String,
    val icono: String? = null
)

// OJO: También debes quitar el usuarioId de tu modelo de dominio (Materia)
fun MateriaEntity.toDomain(): Materia = Materia(id, nombre, color, icono)
fun Materia.toEntity(): MateriaEntity = MateriaEntity(id, nombre, color, icono)