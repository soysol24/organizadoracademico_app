package com.example.organizadoracademico.data.local.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // Creamos el archivo de preferencias llamado "user_session"
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_USER_NAME = "current_user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Guarda los datos del usuario tras un login o registro exitoso.
     */
    fun saveSession(userId: Int, nombre: String) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, nombre)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply() // Guarda los cambios de forma asíncrona
        }
    }

    /**
     * Recupera el ID del usuario actual.
     * Retorna -1 si no hay ninguna sesión activa.
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Recupera el nombre del usuario para mostrarlo en el Perfil o Main.
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, "Estudiante")
    }

    /**
     * Verifica si hay un usuario con sesión iniciada.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Borra todos los datos (útil para el botón de Logout).
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}