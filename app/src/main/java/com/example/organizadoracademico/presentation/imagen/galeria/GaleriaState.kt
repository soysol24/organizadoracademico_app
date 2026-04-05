package com.example.organizadoracademico.presentation.imagen.galeria

import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.model.Materia

data class GaleriaState(
    val materia: Materia? = null,
    val imagenes: List<Imagen> = emptyList(),
    val imagenesAgrupadas: Map<String, List<Imagen>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class GaleriaEvent {
    data class CargarImagenes(val materiaId: Int) : GaleriaEvent()
    data class EliminarImagen(val imagenId: Int) : GaleriaEvent()
    object ResetError : GaleriaEvent()
}