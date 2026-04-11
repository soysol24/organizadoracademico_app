package com.example.organizadoracademico.presentation.imagen.detalle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleImagenScreen(navController: NavController, imagenId: Int) {
    val viewModel: DetalleImagenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.onEvent(DetalleImagenEvent.CargarImagen(imagenId))
    }

    LaunchedEffect(state.eliminado) {
        if (state.eliminado) {
            navController.popBackStack()
        }
    }

// Diálogo de confirmación para eliminar (estilo oscuro)
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text(
                    text = "Eliminar imagen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar esta imagen?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        viewModel.onEvent(DetalleImagenEvent.EliminarImagen)
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFEF4444), // Rojo
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text(
                        text = "Cancelar",
                        color = Color(0xFF6681EA), // Morado como el tema
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color(0xFF1A1A2E), // Fondo oscuro como las cards
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
            // Header - SIN BOTÓN DE ELIMINAR
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
                    text = state.nombreMateria.ifEmpty { "DETALLE" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorBase)
                }
            } else if (state.imagen != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Contenedor de la imagen
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
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
                            val bitmap by produceState<Bitmap?>(initialValue = null, state.imagen!!.uri) {
                                value = loadBitmap(context, state.imagen!!.uri)
                            }

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = "Imagen",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
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

                    Spacer(modifier = Modifier.height(20.dp))

                    // Contenedor de la nota
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = colorBase.copy(alpha = 0.3f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (state.isEditando) {
                                NotaEnEdicionModern(state, viewModel)
                            } else {
                                NotaEnLecturaModern(state, viewModel)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // BOTONES GENERALES - SUBIDOS (dentro del scroll, antes del final)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón DESCARTAR
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A4A5A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("DESCARTAR", fontWeight = FontWeight.Medium)
                        }

                        // Botón ELIMINAR - Texto ROJO
                        Button(
                            onClick = { mostrarDialogoEliminar = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A4A5A),
                                contentColor = Color(0xFFE53935) // ROJO
                            )
                        ) {
                            Text(
                                "ELIMINAR",
                                color = Color(0xFFE53935), //
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Imagen no encontrada",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private suspend fun loadBitmap(context: android.content.Context, rawUri: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        when {
            rawUri.startsWith("content://") || rawUri.startsWith("file://") -> {
                context.contentResolver.openInputStream(Uri.parse(rawUri))?.use { BitmapFactory.decodeStream(it) }
            }
            rawUri.startsWith("http://") || rawUri.startsWith("https://") -> {
                URL(rawUri).openStream().use { BitmapFactory.decodeStream(it) }
            }
            else -> {
                context.contentResolver.openInputStream(Uri.parse(rawUri))?.use { BitmapFactory.decodeStream(it) }
            }
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
fun NotaEnLecturaModern(state: DetalleImagenState, viewModel: DetalleImagenViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "NOTA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6681EA)
        )
        IconButton(onClick = { viewModel.onEvent(DetalleImagenEvent.IniciarEdicion) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = Color(0xFF6681EA),
                modifier = Modifier.size(20.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = state.imagen?.nota ?: "No hay nota para esta imagen.",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.8f)
    )
}

@Composable
fun NotaEnEdicionModern(state: DetalleImagenState, viewModel: DetalleImagenViewModel) {
    Text(
        text = "EDITAR NOTA",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF6681EA)
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = state.nota,
        onValueChange = { viewModel.onEvent(DetalleImagenEvent.NotaCambio(it)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6681EA),
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedContainerColor = Color(0xFF1A1A2E),
            unfocusedContainerColor = Color(0xFF1A1A2E),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
    Spacer(modifier = Modifier.height(16.dp))

    // ✅ BOTONES DE EDICIÓN
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón CANCELAR
        Button(
            onClick = { viewModel.onEvent(DetalleImagenEvent.CancelarEdicion) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A5A),
                contentColor = Color.White
            )
        ) {
            Text("CANCELAR", fontWeight = FontWeight.Medium)
        }

        // Botón GUARDAR - Texto VERDE
        Button(
            onClick = { viewModel.onEvent(DetalleImagenEvent.GuardarNota) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A4A5A),
                contentColor = Color(0xFF52B788) // VERDE
            )
        ) {
            Text(
                "GUARDAR",
                color = Color(0xFF52B788), //
                fontWeight = FontWeight.Medium
            )        }
    }
}