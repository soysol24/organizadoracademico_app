package com.example.organizadoracademico.push

import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.dto.PushTokenRequestDto

interface PushTokenUploader {
    suspend fun uploadToken(token: String)
}

class PushTokenUploaderImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : PushTokenUploader {

    override suspend fun uploadToken(token: String) {
        if (!sessionManager.isLoggedIn() || token.isBlank()) return

        val payload = PushTokenRequestDto(token = token)

        // Intentamos endpoint principal y fallback para compatibilidad.
        val primary = runCatching { apiService.registerPushToken(payload) }.getOrNull()
        if (primary?.isSuccessful == true) return

        runCatching { apiService.registerDeviceToken(payload) }
    }
}


