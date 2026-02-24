package com.example.organizadoracademico.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.usercase.usuario.GetUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getUsuarioUseCase: GetUsuarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        cargarUsuario()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CargarDatos -> cargarUsuario()
            else -> {} // Los eventos de navegación los maneja la UI
        }
    }

    private fun cargarUsuario() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Por ahora cargamos usuario por defecto (ID 1)
            val result = getUsuarioUseCase.invoke(1)

            result.onSuccess { usuario ->
                _state.update {
                    it.copy(
                        usuarioNombre = usuario?.nombre ?: "Sol",
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }
}
