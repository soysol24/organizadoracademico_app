package com.example.organizadoracademico.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.usercase.usuario.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = event.email) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginEvent.LoginClicked -> login()
            is LoginEvent.ResetError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase.invoke(_state.value.email, _state.value.password)

            result.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Credenciales incorrectas"
                    )
                }
            }
        }
    }
}