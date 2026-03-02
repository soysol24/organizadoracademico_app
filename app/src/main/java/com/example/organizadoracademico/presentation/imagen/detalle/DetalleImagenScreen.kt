package com.example.organizadoracademico.presentation.imagen.detalle

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleImagenScreen(navController: NavController, imagenId: Int) {
    val viewModel: DetalleImagenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onEvent(DetalleImagenEvent.CargarImagen(imagenId))
    }

    LaunchedEffect(state.eliminado) {
        if (state.eliminado) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.nombreMateria.ifEmpty { "Detalle" }, color = TextoBlanco) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, color = TextoBlanco)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(DetalleImagenEvent.EliminarImagen) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RosaNeon)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperficieCards)
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(FondoPrincipal), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MoradoNeon)
            }
        } else if (state.imagen != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FondoPrincipal)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Contenedor de la Imagen
                val bitmap = remember(state.imagen!!.uri) {
                    try {
                        context.contentResolver.openInputStream(Uri.parse(state.imagen!!.uri))?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SuperficieCards)
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Imagen de la nota",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("📸", fontSize = 80.sp) // Placeholder en caso de error
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Contenedor de la Nota
                if (state.isEditando) {
                    NotaEnEdicion(state, viewModel)
                } else {
                    NotaEnLectura(state, viewModel)
                }

                 if (state.errorMessage != null) {
                    Text(state.errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
                }
            }
        } else {
             Box(modifier = Modifier.fillMaxSize().background(FondoPrincipal), contentAlignment = Alignment.Center) {
                Text("Imagen no encontrada", color = TextoGris, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun NotaEnLectura(state: DetalleImagenState, viewModel: DetalleImagenViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("NOTA", style = MaterialTheme.typography.titleMedium, color = TextoBlanco)
            IconButton(onClick = { viewModel.onEvent(DetalleImagenEvent.IniciarEdicion) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Nota", tint = MoradoNeon)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(state.imagen?.nota ?: "No hay nota para esta imagen.", color = TextoGris, fontSize = 16.sp)
    }
}

@Composable
fun NotaEnEdicion(state: DetalleImagenState, viewModel: DetalleImagenViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("EDITAR NOTA", style = MaterialTheme.typography.titleMedium, color = TextoBlanco)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.nota,
            onValueChange = { viewModel.onEvent(DetalleImagenEvent.NotaCambio(it)) },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            )
        )
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { viewModel.onEvent(DetalleImagenEvent.CancelarEdicion) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuperficieCards)
            ) { Text("CANCELAR", color = TextoBlanco) }

            Button(
                onClick = { viewModel.onEvent(DetalleImagenEvent.GuardarNota) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeExito)
            ) { Text("GUARDAR", color = TextoBlanco) }
        }
    }
}