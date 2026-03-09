package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Profesor

@Entity(tableName = "profesores",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class ProfesorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)

// OJO: También debes quitar el usuarioId de tu modelo de dominio (Profesor)
fun ProfesorEntity.toDomain(): Profesor = Profesor(id, nombre)
fun Profesor.toEntity(): ProfesorEntity = ProfesorEntity(id, nombre)