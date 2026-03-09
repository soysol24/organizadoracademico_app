package com.example.organizadoracademico.presentation.imagen.camara

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.organizadoracademico.hardware.camera.CameraManager
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.*
import org.koin.androidx.compose.koinViewModel
import com.example.organizadoracademico.presentation.animation.pulseEffect
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CamaraScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: CamaraViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraManager = remember { CameraManager(context) }

    var hasPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    // MODIFICACIÓN 1: Limpiamos todo al entrar para que no se vea la foto anterior
    LaunchedEffect(Unit) {
        viewModel.onEvent(CamaraEvent.LimpiarTodo) // <-- Limpia estados viejos
        viewModel.onEvent(CamaraEvent.Inicializar(materiaId))
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Efecto para navegar
    LaunchedEffect(state.photoSaved) {
        if (state.photoSaved && !state.lastPhotoUri.isNullOrBlank()) {
            try {
                val encodedUri = URLEncoder.encode(state.lastPhotoUri, StandardCharsets.UTF_8.toString())
                navController.navigate(Screen.Nota.passParams(materiaId, encodedUri))

                // IMPORTANTE: ResetNavegacion ahora NO borra la foto,
                // así que no habrá parpadeo mientras cambia la pantalla.
                viewModel.onEvent(CamaraEvent.ResetNavegacion)

            } catch (e: Exception) {
                viewModel.onEvent(CamaraEvent.ResetError)
                viewModel.onEvent(CamaraEvent.ResetNavegacion)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {
        if (hasPermission) {
            // MODIFICACIÓN 2: Cambiamos la lógica del IF
            // Si hay una foto capturada, mostramos el Preview.
            // Si NO hay foto (null), mostramos la cámara activa.
            if (state.lastPhotoUri != null) {
                PhotoPreview(viewModel)
            } else {
                cameraManager.CameraPreview(
                    onImageCaptured = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                    onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                )

                Button(
                    onClick = {
                        cameraManager.takePhoto(
                            onSuccess = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                            onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                        )
                    },
                    enabled = !state.isTakingPhoto,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MoradoNeon),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(72.dp)
                        .pulseEffect(!state.isTakingPhoto)
                ) {
                    Text("📷", fontSize = 32.sp)
                }
            }
        } else {
            PermissionDeniedView(navController)
        }

        // Botón de cerrar
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SuperficieCards.copy(alpha = 0.8f))
        ) {
            Text("←", fontSize = 24.sp, color = TextoBlanco)
        }
    }
}

@Composable
fun PhotoPreview(viewModel: CamaraViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SuperficieCards),
            contentAlignment = Alignment.Center
        ) {
            Text("📸", fontSize = 80.sp)
        }
        Spacer(Modifier.height(32.dp))
        Text("¿QUIERES GUARDAR ESTA FOTO?", fontSize = 16.sp, color = TextoBlanco)
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.onEvent(CamaraEvent.DescartarFoto) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuperficieCards)
            ) {
                Text("DESCARTAR", color = TextoBlanco)
            }
            Button(
                onClick = { viewModel.onEvent(CamaraEvent.ContinuarConNota) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoNeon)
            ) {
                Text("CONTINUAR", color = TextoBlanco)
            }
        }
    }
}

@Composable
fun PermissionDeniedView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Permiso de cámara denegado", color = TextoBlanco, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text("Necesitas conceder el permiso para usar la cámara.", color = TextoGris, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("VOLVER")
        }
    }
}