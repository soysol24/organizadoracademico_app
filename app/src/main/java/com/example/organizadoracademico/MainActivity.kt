package com.example.organizadoracademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.organizadoracademico.presentation.navigation.NavGraph
import com.example.organizadoracademico.presentation.theme.OrganizadorAcademicoTheme
import com.example.organizadoracademico.presentation.theme.FondoPrincipal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrganizadorAcademicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoPrincipal
                ) {
                    NavGraph()
                }
            }
        }
    }
}