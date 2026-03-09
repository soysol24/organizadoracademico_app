package com.example.organizadoracademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.presentation.navigation.NavGraph
import com.example.organizadoracademico.presentation.theme.OrganizadorAcademicoTheme
import com.example.organizadoracademico.presentation.theme.FondoPrincipal
import org.koin.android.ext.android.inject // Para inyectar el manager

class MainActivity : ComponentActivity() {

    // Inyectamos el SessionManager usando Koin
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrganizadorAcademicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoPrincipal
                ) {
                    // Verificamos si hay sesión activa
                    val estaLogueado = sessionManager.isLoggedIn()

                    // Pasamos esta bandera a tu NavGraph para que sepa
                    // si mostrar el Login o el Home directamente.
                    NavGraph(isLoggedIn = estaLogueado)
                }
            }
        }
    }
}