package com.example.organizadoracademico

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import com.example.organizadoracademico.presentation.navigation.NavGraph
import com.example.organizadoracademico.presentation.theme.OrganizadorAcademicoTheme
import com.example.organizadoracademico.presentation.theme.FondoPrincipal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Eliminamos el lifecycleScope y la inserción manual
        // porque ahora AppDatabase lo hace automáticamente al inicio.

        setContent {
            OrganizadorAcademicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoPrincipal
                ) {
                    // Tu enrutador de pantallas
                    NavGraph()
                }
            }
        }
    }
}