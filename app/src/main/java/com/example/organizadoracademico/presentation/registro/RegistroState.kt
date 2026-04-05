package com.example.organizadoracademico.presentation.registro

data class RegistroState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val registroExitoso: Boolean = false,
    val errorMessage: String? = null
)

sealed class RegistroEvent {
    data class NombreCambio(val nombre: String) : RegistroEvent()
    data class EmailCambio(val email: String) : RegistroEvent()
    data class PasswordCambio(val password: String) : RegistroEvent()
    data class ConfirmPasswordCambio(val password: String) : RegistroEvent()
    object Registrar : RegistroEvent()
    object ResetError : RegistroEvent()
}