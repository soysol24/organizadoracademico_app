package com.example.organizadoracademico.presentation.perfil

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)
    val neonGlow = colorSecundario.copy(alpha = 0.5f)
    val cardDark = Color(0xFF1A1A2E)

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Navegar al login cuando el usuario sea nulo (después de cerrar sesión)
    LaunchedEffect(state.usuario, state.isLoading) {
        if (state.usuario == null && !state.isLoading) {
            navController.navigate(Screen.Login.route) {
                // Limpia toda la pila de navegación para que no se pueda volver atrás
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres cerrar sesión?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        viewModel.onEvent(PerfilEvent.CerrarSesion)
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text(
                        text = "Cancelar",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = cardDark,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBase)
    ) {
        // Gradiente animado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorSecundario.copy(alpha = 0.9f),
                            colorSecundario.copy(alpha = 0.5f),
                            Color.Transparent
                        ),
                        start = Offset(animatedOffset * 1500f, 0f),
                        end = Offset((animatedOffset + 0.4f) * 1500f, 1200f)
                    )
                )
        )

        // Elementos flotantes
        repeat(12) { index ->
            val animatedY by infiniteTransition.animateFloat(
                initialValue = (-150).dp.value,
                targetValue = 1500.dp.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (4000 + index * 800),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (30 + index * 70).dp,
                        y = animatedY.dp
                    )
                    .size((12 + index * 6).dp)
                    .background(
                        Color.White.copy(alpha = 0.12f),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con flecha atrás y título centrado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Flecha atrás (izquierda)
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }

                // Título centrado
                Text(
                    text = "PERFIL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(
                text = state.usuario?.nombre?.uppercase() ?: "USUARIO",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Text(
                text = state.usuario?.email ?: "correo@ejemplo.com",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Card para CERRAR SESIÓN (estilo primera imagen - solo ícono + texto)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = neonGlow,
                        spotColor = neonGlow
                    )
                    .clickable { mostrarDialogoCerrarSesion = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = colorBase ,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "CERRAR SESIÓN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ir",
                        tint = colorBase ,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.errorMessage!!,
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = colorBase, modifier = Modifier.size(32.dp))
            }
        }
    }
}