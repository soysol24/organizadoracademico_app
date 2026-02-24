package com.example.organizadoracademico.presentation.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.usercase.usuario.RegistroUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val registroUseCase: RegistroUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroState())
    val state: StateFlow<RegistroState> = _state.asStateFlow()

    fun onEvent(event: RegistroEvent) {
        when (event) {
            is RegistroEvent.NombreCambio -> {
                _state.update { it.copy(nombre = event.nombre) }
            }
            is RegistroEvent.EmailCambio -> {
                _state.update { it.copy(email = event.email) }
            }
            is RegistroEvent.PasswordCambio -> {
                _state.update { it.copy(password = event.password) }
            }
            is RegistroEvent.ConfirmPasswordCambio -> {
                _state.update { it.copy(confirmPassword = event.password) }
            }
            is RegistroEvent.Registrar -> {
                registrar()
            }
            is RegistroEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun registrar() {
        viewModelScope.launch {
            // Validaciones
            if (_state.value.nombre.isBlank()) {
                _state.update { it.copy(errorMessage = "El nombre es requerido") }
                return@launch
            }
            if (_state.value.email.isBlank()) {
                _state.update { it.copy(errorMessage = "El email es requerido") }
                return@launch
            }
            if (_state.value.password.length < 6) {
                _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return@launch
            }
            if (_state.value.password != _state.value.confirmPassword) {
                _state.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = registroUseCase.invoke(
                nombre = _state.value.nombre,
                email = _state.value.email,
                password = _state.value.password
            )

            result.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        registroExitoso = true,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al registrar"
                    )
                }
            }
        }
    }
}
