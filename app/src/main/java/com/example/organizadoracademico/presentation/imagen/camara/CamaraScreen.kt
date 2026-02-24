package com.example.organizadoracademico.presentation.imagen.camara

import androidx.compose.foundation.background
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
import com.example.organizadoracademico.hardware.camera.CameraManager
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun CamaraScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: CamaraViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraManager = remember { CameraManager(context) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(CamaraEvent.Inicializar(materiaId))
    }

    // Navegar a pantalla de nota cuando se guarda la foto
    LaunchedEffect(state.photoSaved) {
        if (state.photoSaved && state.lastPhotoUri != null) {
            navController.navigate(
                Screen.Nota.passParams(materiaId, state.lastPhotoUri!!)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {
        if (state.lastPhotoUri == null) {
            // Vista de cámara
            cameraManager.CameraPreview(
                onImageCaptured = { uri ->
                    viewModel.onEvent(CamaraEvent.FotoTomada(uri))
                },
                onError = { error ->
                    // Manejar error
                }
            )

            // Botón para tomar foto
            Button(
                onClick = { viewModel.onEvent(CamaraEvent.TomarFoto) },
                enabled = !state.isTakingPhoto,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoNeon
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(72.dp)
                    .pulseEffect(!state.isTakingPhoto)
            ) {
                if (state.isTakingPhoto) {
                    CircularProgressIndicator(
                        color = TextoBlanco,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = "📷",
                        fontSize = 32.sp
                    )
                }
            }
        } else {
            // Vista previa de la foto tomada
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Miniaturas
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SuperficieCards),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📸",
                        fontSize = 80.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "¿QUIERES AGREGAR UNA NOTA?",
                    fontSize = 16.sp,
                    color = TextoBlanco
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(CamaraEvent.DescartarFoto) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuperficieCards
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(
                            text = "DESCARTAR",
                            color = TextoGris
                        )
                    }

                    Button(
                        onClick = { viewModel.onEvent(CamaraEvent.ContinuarConNota) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeMatrix
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .pulseEffect(true)
                    ) {
                        Text(
                            text = "CONTINUAR",
                            color = TextoBlanco
                        )
                    }
                }
            }
        }

        // Botón de cerrar
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SuperficieCards)
        ) {
            Text("←", fontSize = 24.sp, color = TextoBlanco)
        }

        // Mensaje de error
        if (state.errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.onEvent(CamaraEvent.ResetError) }
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