package com.example.organizadoracademico.presentation.imagen.detalle

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun DetalleImagenScreen(navController: NavController, imagenId: Int) {
    Text("Pantalla de Detalle de Imagen para el ID: $imagenId")
}