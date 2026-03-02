package com.example.organizadoracademico.presentation.imagen.nota

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.theme.*
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
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AGREGAR NOTA", color = TextoBlanco) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, color = TextoBlanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperficieCards)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SuperficieCards),
                contentAlignment = Alignment.Center
            ) {
                if (decodedUri.isNotEmpty()) {
                    val imagePath = Uri.parse(decodedUri).path
                    if (imagePath != null) {
                        val bitmap = BitmapFactory.decodeFile(imagePath)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Vista previa de la foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Error al cargar imagen", color = Color.Red)
                        }
                    } else {
                         Text("Ruta de imagen inválida", color = Color.Red)
                    }
                } else {
                    Text("📸", fontSize = 80.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("ESCRIBE UNA NOTA", style = MaterialTheme.typography.titleMedium, color = TextoBlanco)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.nota,
                onValueChange = { viewModel.onEvent(NotaEvent.NotaCambio(it)) },
                placeholder = { Text("¿Qué aprendiste en esta clase?\nEj: Entendí el concepto de ViewModel...", color = TextoGris) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MoradoNeon,
                    unfocusedBorderColor = BordeNeon,
                    focusedContainerColor = SuperficieCards,
                    unfocusedContainerColor = SuperficieCards,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Text(
                text = "${state.nota.length}/${NotaState.MAX_CHARS}",
                color = if (state.caracteresRestantes >= 0) TextoGris else Color.Red,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(NotaEvent.SaltarNota) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuperficieCards)
                ) { 
                    Text("SALTAR", color = TextoBlanco) 
                }
                Button(
                    onClick = { viewModel.onEvent(NotaEvent.GuardarNota) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeExito)
                ) { 
                    Text("GUARDAR", color = TextoBlanco) 
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(color = MoradoNeon)
            }
        }
    }
}