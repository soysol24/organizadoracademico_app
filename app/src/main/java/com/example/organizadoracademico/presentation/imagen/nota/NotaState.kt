package com.example.organizadoracademico.presentation.imagen.nota

data class NotaState(
    val materiaId: Int = 0,
    val imageUri: String = "",
    val nota: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        const val MAX_CHARS = 200 // Límite de caracteres
    }
    val caracteresRestantes: Int
        get() = MAX_CHARS - nota.length
}

sealed class NotaEvent {
    data class Inicializar(val materiaId: Int, val imageUri: String) : NotaEvent()
    data class NotaCambio(val nota: String) : NotaEvent()
    object GuardarNota : NotaEvent()
    object SaltarNota : NotaEvent()
    object ResetError : NotaEvent()
}