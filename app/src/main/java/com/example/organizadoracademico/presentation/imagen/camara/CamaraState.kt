package com.example.organizadoracademico.presentation.imagen.camara

data class CamaraState(
    val materiaId: Int = 0,
    val isTakingPhoto: Boolean = false,
    val isSaving: Boolean = false,
    val lastPhotoUri: String? = null,
    val errorMessage: String? = null,
    val photoSaved: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val shouldShowPermissionDialog: Boolean = false
)

sealed class CamaraEvent {
    data class Inicializar(val materiaId: Int) : CamaraEvent()
    object TomarFoto : CamaraEvent()
    data class FotoTomada(val uri: String) : CamaraEvent()
    object ContinuarConNota : CamaraEvent()
    object DescartarFoto : CamaraEvent()
    object SolicitarPermiso : CamaraEvent()
    object PermisoConcedido : CamaraEvent()
    object PermisoDenegado : CamaraEvent()
    object ResetError : CamaraEvent()
}