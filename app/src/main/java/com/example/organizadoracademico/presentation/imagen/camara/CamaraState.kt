package com.example.organizadoracademico.presentation.imagen.camara

data class CamaraState(
    val materiaId: Int = 0,
    val isTakingPhoto: Boolean = false,
    val lastPhotoUri: String? = null,
    val errorMessage: String? = null,
    val photoSaved: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val isInitialized: Boolean = false
)

sealed class CamaraEvent {
    // Vuelve a ser simple, el ViewModel solo necesita el ID.
    data class Inicializar(val materiaId: Int) : CamaraEvent()

    // La UI notifica al ViewModel cuando una foto se ha tomado.
    data class FotoTomada(val uri: String) : CamaraEvent()

    // Eventos de la UI para la vista previa de la foto.
    object ContinuarConNota : CamaraEvent()
    object DescartarFoto : CamaraEvent()
    object ResetError : CamaraEvent()
}