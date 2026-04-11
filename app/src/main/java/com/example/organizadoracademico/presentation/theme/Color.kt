package com.example.organizadoracademico.presentation.theme

import androidx.compose.ui.graphics.Color

// Fondo - Morado oscuro
val FondoPrincipal = Color(0xFF1A0B2E)
val SuperficieCards = Color(0xFF2D1B45)

// Acentos NEON
val MoradoNeon = Color(0xFFB967FF)
val AzulElectrico = Color(0xFF4361EE)
val VerdeMatrix = Color(0xFF52B788)
val NaranjaNeon = Color(0xFFF48C06)
val RosaNeon = Color(0xFFE83F6F)
val VerdeExito = Color(0xFF2ECC71) // <-- COLOR AÑADIDO

// Texto
val TextoBlanco = Color(0xFFFFFFFF)
val TextoGris = Color(0xFFB7B7C9)

// Bordes
val BordeNeon = Color(0xFF3D2A5A)

object AppColors {
    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)
    val cardDark = Color(0xFF1A1A2E)
    
    fun fromNombre(nombre: String): Color {
        return when (nombre) {
            "Morado" -> Color(0xFFB967FF)
            "Azul" -> Color(0xFF4361EE)
            "Verde" -> Color(0xFF52B788)
            "Naranja" -> Color(0xFFF48C06)
            "Rojo" -> Color(0xFFE83F6F)
            else -> Color(0xFFB967FF)
        }
    }
}