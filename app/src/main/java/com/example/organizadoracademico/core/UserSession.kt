package com.example.organizadoracademico.core

object UserSession {
    // Almacena el ID del usuario logueado
    var userId: Int? = null

    // Helper para saber si hay alguien logueado
    val isLoggedIn: Boolean get() = userId != null

    // Limpia la sesión al cerrar cuenta
    fun logout() {
        userId = null
    }
}