package com.example.organizadoracademico.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.organizadoracademico.domain.model.Imagen

@Entity(
    tableName = "imagenes",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["materiaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImagenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materiaId: Int,
    val uri: String,
    val nota: String? = null,
    val fecha: Long = System.currentTimeMillis(),
    val favorita: Boolean = false
)

fun ImagenEntity.toDomain(): Imagen = Imagen(id, materiaId, uri, nota, fecha, favorita)
fun Imagen.toEntity(): ImagenEntity = ImagenEntity(id, materiaId, uri, nota, fecha, favorita)