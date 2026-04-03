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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.organizadoracademico.presentation.animation.pulseEffect
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(
    navController: NavController, 
    materiaId: Int, 
    viewModel: GaleriaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(GaleriaEvent.CargarImagenes(materiaId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.materia?.nombre?.uppercase() ?: "GALERÍA", color = TextoBlanco) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, color = TextoBlanco)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Camara.passMateriaId(materiaId)) }) {
                        Text("📷", fontSize = 20.sp, color = MoradoNeon)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperficieCards)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MoradoNeon)
                }
            } else if (state.imagenes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📸", fontSize = 64.sp)
                        Text("No hay imágenes aún", fontSize = 18.sp, color = TextoGris, modifier = Modifier.padding(top = 16.dp))
                        Text("Toca el ícono de cámara para agregar", fontSize = 14.sp, color = TextoGris)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    state.imagenesAgrupadas.toList().forEach { (fecha, imagenes) ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            FechaHeader(fecha = fecha)
                        }
                        items(imagenes) { imagen ->
                            ImagenThumbnail(
                                imagen = imagen,
                                onClick = { navController.navigate(Screen.DetalleImagen.passImagenId(imagen.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FechaHeader(fecha: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⚡", fontSize = 14.sp, color = MoradoNeon)
        Text(fecha.uppercase(), fontSize = 16.sp, color = TextoBlanco, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(MoradoNeon))
    }
}

@Composable
fun ImagenThumbnail(
    imagen: com.example.organizadoracademico.domain.model.Imagen,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .pulseEffect(true)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCards)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = imagen.uri,
                contentDescription = "Miniatura de la imagen",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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
