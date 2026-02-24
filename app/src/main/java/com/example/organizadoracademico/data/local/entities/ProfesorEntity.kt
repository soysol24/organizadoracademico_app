package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Profesor

@Entity(tableName = "profesores")
data class ProfesorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)

fun ProfesorEntity.toDomain(): Profesor = Profesor(id, nombre)
fun Profesor.toEntity(): ProfesorEntity = ProfesorEntity(id, nombre)