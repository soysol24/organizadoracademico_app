package com.example.organizadoracademico.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.*
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {
        // Header
        HeaderSection(
            nombre = state.usuarioNombre,
            onPerfilClick = { navController.navigate(Screen.Perfil.route) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de bienvenida
        WelcomeCard()

        Spacer(modifier = Modifier.height(32.dp))

        // Título de acciones
        Text(
            text = "ACCIONES RÁPIDAS",
            fontSize = 18.sp,
            color = MoradoNeon,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Menú de acciones (SOLO 3 OPCIONES)
        ActionMenu(
            onCrearHorario = { navController.navigate(Screen.CrearHorario.route) },
            onVerHorario = { navController.navigate(Screen.VerHorario.route) },
            onMisMaterias = { navController.navigate(Screen.MisMaterias.route) }
            // ELIMINADO: onAjustes
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation (SOLO 2 OPCIONES)
        BottomNavigationBar(
            onInicioClick = { /* Ya estamos en inicio */ },
            onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            // ELIMINADO: onAjustesClick
        )
    }
}

@Composable
fun HeaderSection(
    nombre: String,
    onPerfilClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "¡HOLA DE NUEVO",
                fontSize = 14.sp,
                color = TextoGris
            )
            Text(
                text = nombre.uppercase(),
                fontSize = 24.sp,
                color = TextoBlanco,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MoradoNeon.copy(alpha = 0.2f))
                .pulseEffect(true)
                .clickable { onPerfilClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡TE ECHAMOS DE MENOS!",
                    fontSize = 18.sp,
                    color = TextoBlanco,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Organiza tu horario para esta semana",
                    fontSize = 14.sp,
                    color = TextoGris,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "📅",
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun ActionMenu(
    onCrearHorario: () -> Unit,
    onVerHorario: () -> Unit,
    onMisMaterias: () -> Unit
    // ELIMINADO: onAjustes
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        ActionButton(
            icon = "➕",
            title = "CREAR HORARIO",
            subtitle = "Agrega tus clases",
            color = MoradoNeon,
            onClick = onCrearHorario
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionButton(
            icon = "📅",
            title = "VER MI HORARIO",
            subtitle = "Mira tu semana",
            color = AzulElectrico,
            onClick = onVerHorario
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionButton(
            icon = "📸",
            title = "MIS MATERIAS",
            subtitle = "Ver tus apuntes",
            color = VerdeMatrix,
            onClick = onMisMaterias
        )

        // ELIMINADO: Bloque de CONFIGURACIÓN
    }
}

@Composable
fun ActionButton(
    icon: String,
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pulseEffect(true)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = TextoBlanco,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = TextoGris
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onInicioClick: () -> Unit,
    onPerfilClick: () -> Unit
    // ELIMINADO: onAjustesClick
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SuperficieCards,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onInicioClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏠",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Inicio",
                        fontSize = 12.sp,
                        color = MoradoNeon
                    )
                }
            }

            IconButton(onClick = onPerfilClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "👤",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Perfil",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }
            }

            // ELIMINADO: IconButton de Ajustes
        }
    }
}