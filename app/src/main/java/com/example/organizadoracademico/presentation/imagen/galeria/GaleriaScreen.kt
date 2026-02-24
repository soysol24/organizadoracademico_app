package com.example.organizadoracademico.presentation.imagen.galeria

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.animation.pulseEffect
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla que muestra todas las imágenes de una materia específica
 * en formato de grid con agrupación por fecha
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(
    navController: NavController,        // Controlador de navegación
    materiaId: Int,                      // ID de la materia seleccionada
    viewModel: GaleriaViewModel = koinViewModel()  // ViewModel inyectado
) {
    val state by viewModel.state.collectAsState()  // Estado observable

    // Carga inicial de imágenes al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.onEvent(GaleriaEvent.CargarImagenes(materiaId))
    }

    // Estructura principal con barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.materia?.nombre?.uppercase() ?: "GALERÍA",
                        color = TextoBlanco
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, color = TextoBlanco)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Navega a la pantalla de cámara para tomar nueva foto
                            navController.navigate(Screen.Camara.passMateriaId(materiaId))
                        }
                    ) {
                        Text("📷", fontSize = 20.sp, color = MoradoNeon)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SuperficieCards
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
        ) {
            // Estado de carga
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MoradoNeon)
                }
            }
            // Estado vacío (sin imágenes)
            else if (state.imagenes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📸",
                            fontSize = 64.sp
                        )
                        Text(
                            text = "No hay imágenes aún",
                            fontSize = 18.sp,
                            color = TextoGris,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Toca el ícono de cámara para agregar",
                            fontSize = 14.sp,
                            color = TextoGris
                        )
                    }
                }
            }
            // Grid de imágenes (contenido principal)
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),  // 3 columnas
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Itera sobre las imágenes agrupadas por fecha
                    state.imagenesAgrupadas.toList().forEach { (fecha, imagenes) ->
                        // Encabezado de fecha (ocupa todo el ancho)
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            FechaHeader(fecha = fecha)
                        }
                        // Miniaturas de imágenes de esa fecha
                        items(imagenes) { imagen ->
                            ImagenThumbnail(
                                imagen = imagen,
                                onClick = {
                                    // Navega al detalle de la imagen seleccionada
                                    navController.navigate(
                                        Screen.DetalleImagen.passImagenId(imagen.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Snackbar para mostrar errores
            if (state.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(
                            onClick = { viewModel.onEvent(GaleriaEvent.ResetError) }
                        ) {
                            Text("OK", color = MoradoNeon)
                        }
                    }
                ) {
                    Text(state.errorMessage!!, color = TextoBlanco)
                }
            }
        }
    }
}

/**
 * Componente que muestra un encabezado con la fecha
 * @param fecha Texto de la fecha a mostrar
 */
@Composable
fun FechaHeader(fecha: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⚡",
            fontSize = 14.sp,
            color = MoradoNeon
        )
        Text(
            text = fecha.uppercase(),
            fontSize = 16.sp,
            color = TextoBlanco,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MoradoNeon)
        )
    }
}

/**
 * Componente que muestra una miniatura de imagen
 * @param imagen Modelo de la imagen a mostrar
 * @param onClick Función a ejecutar al hacer clic en la miniatura
 */
@Composable
fun ImagenThumbnail(
    imagen: com.example.organizadoracademico.domain.model.Imagen,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)  // Mantiene relación de aspecto cuadrada
            .pulseEffect(true)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Representación visual de la imagen (placeholder)
            Text(
                text = "📸",
                fontSize = 24.sp
            )

            // Indicador visual de que la imagen tiene nota
            if (imagen.nota != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(VerdeMatrix)
                )
            }
        }
    }
}