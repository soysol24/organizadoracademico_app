package com.example.organizadoracademico.presentation.imagen.nota

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaScreen(
    navController: NavController,
    materiaId: Int,
    imageUri: String,
    viewModel: NotaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val decodedUri = remember(imageUri) {
        try {
            URLDecoder.decode(imageUri, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(NotaEvent.Inicializar(materiaId, decodedUri))
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            navController.navigate(Screen.Galeria.passMateriaId(materiaId)) {
                popUpTo(Screen.Camara.route) { inclusive = true }
            }
        }
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
                            colorSecundario.copy(alpha = 0.4f),
                            colorSecundario.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        start = Offset(animatedOffset * 1500f, 0f),
                        end = Offset((animatedOffset + 0.5f) * 1500f, 1200f)
                    )
                )
        )

        // Elementos flotantes
        repeat(8) { index ->
            val animatedY by infiniteTransition.animateFloat(
                initialValue = (-100).dp.value,
                targetValue = 1200.dp.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (8000 + index * 1000),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (40 + index * 100).dp,
                        y = animatedY.dp
                    )
                    .size((10 + index * 5).dp)
                    .background(
                        Color.White.copy(alpha = 0.06f),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "AGREGAR NOTA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Vista previa de la imagen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = colorBase.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (decodedUri.isNotEmpty()) {
                        val imagePath = Uri.parse(decodedUri).path
                        if (imagePath != null) {
                            val bitmap = BitmapFactory.decodeFile(imagePath)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Vista previa",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text("Error al cargar imagen", color = Color.Red)
                            }
                        } else {
                            Text("Ruta inválida", color = Color.Red)
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Vista previa",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de nota - TEXTO EN BLANCO
            Text(
                text = "ESCRIBE UNA NOTA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White, // ← CAMBIADO A BLANCO
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.nota,
                onValueChange = { viewModel.onEvent(NotaEvent.NotaCambio(it)) },
                placeholder = {
                    Text(
                        "¿Qué aprendiste en esta clase?\nEj: Entendí el concepto de ViewModel...",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorBase,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color(0xFF1A1A2E),
                    unfocusedContainerColor = Color(0xFF1A1A2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Text(
                text = "${state.nota.length}/${NotaState.MAX_CHARS}",
                fontSize = 12.sp,
                color = if (state.caracteresRestantes >= 0) Color.White.copy(alpha = 0.5f) else Color(0xFFEF4444),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones (sin bottom bar)
            // Botones (sin bottom bar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón SALTAR - Fondo gris, letras ROSITAS
                Button(
                    onClick = { viewModel.onEvent(NotaEvent.SaltarNota) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A5A), // Fondo gris
                        contentColor = Color(0xFFFF6B6B) // ROSITA
                    )
                ) {
                    Text("SALTAR", fontWeight = FontWeight.Medium)
                }

                // Botón GUARDAR - Fondo gris, letras VERDES
                Button(
                    onClick = { viewModel.onEvent(NotaEvent.GuardarNota) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A5A), // Fondo gris
                        contentColor = Color(0xFF52B788) // VERDE
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF52B788),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("GUARDAR", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}