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
        ),
        // --- NUEVA LLAVE FORÁNEA CON EL USUARIO ---
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HorarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["horarioId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ImagenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: Int? = null,
    val materiaId: Int,
    val usuarioId: Int, // <-- AÑADIDO: Dueño de la foto
    val horarioId: Int? = null,
    val uri: String,
    val nota: String? = null,
    val fecha: Long = System.currentTimeMillis(),
    val favorita: Boolean = false
)

// Mapeadores actualizados
fun ImagenEntity.toDomain(): Imagen = Imagen(
    id, materiaId, usuarioId, horarioId, uri, nota, fecha, favorita
)

fun Imagen.toEntity(): ImagenEntity = ImagenEntity(
    id = id,
    materiaId = materiaId,
    usuarioId = usuarioId,
    horarioId = horarioId,
    uri = uri,
    nota = nota,
    fecha = fecha,
    favorita = favorita
)