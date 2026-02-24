package com.example.organizadoracademico.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = darkColorScheme(
    primary = MoradoNeon,
    secondary = AzulElectrico,
    tertiary = RosaNeon,
    background = FondoPrincipal,
    surface = SuperficieCards,
    onPrimary = TextoBlanco,
    onSecondary = TextoBlanco,
    onBackground = TextoBlanco,
    onSurface = TextoGris
)

@Composable
fun OrganizadorAcademicoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Tipografia,
        content = content
    )
}