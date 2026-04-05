package com.example.organizadoracademico.presentation.imagen.detalle

import com.example.organizadoracademico.domain.model.Imagen

data class DetalleImagenState(
    val isLoading: Boolean = true,
    val imagen: Imagen? = null,
    val nombreMateria: String = "", // <-- CAMPO AÑADIDO
    val nota: String = "",
    val isEditando: Boolean = false,
    val eliminado: Boolean = false,
    val errorMessage: String? = null
)

sealed class DetalleImagenEvent {
    data class CargarImagen(val id: Int) : DetalleImagenEvent()
    object IniciarEdicion : DetalleImagenEvent()
    object CancelarEdicion : DetalleImagenEvent()
    data class NotaCambio(val nota: String) : DetalleImagenEvent()
    object GuardarNota : DetalleImagenEvent()
    object ToggleFavorita : DetalleImagenEvent()
    object EliminarImagen : DetalleImagenEvent()
    object ResetError : DetalleImagenEvent()
}