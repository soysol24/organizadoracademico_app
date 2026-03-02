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
    // Usamos remember para que el CameraManager persista durante recomposiciones
    val cameraManager = remember { CameraManager(context) }

    var hasPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    // Se ejecuta una sola vez para inicializar el ViewModel y pedir permisos
    LaunchedEffect(Unit) {
        viewModel.onEvent(CamaraEvent.Inicializar(materiaId))
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Efecto para navegar cuando la foto se marca como "guardada"
    LaunchedEffect(state.photoSaved) {
        if (state.photoSaved && state.lastPhotoUri != null) {
            try {
                // Se encodifica la URI para que sea seguro pasarla como argumento de navegación
                val encodedUri = URLEncoder.encode(state.lastPhotoUri, StandardCharsets.UTF_8.toString())
                navController.navigate(Screen.Nota.passParams(materiaId, encodedUri))
            } catch (e: Exception) {
                viewModel.onEvent(CamaraEvent.ResetError) // Manejar error de navegación
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {
        if (hasPermission) {
            if (state.lastPhotoUri == null) {
                // Vista de la cámara activa
                cameraManager.CameraPreview(
                    onImageCaptured = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                    onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                )

                // Botón para tomar la foto
                Button(
                    onClick = { cameraManager.takePhoto(
                        onSuccess = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                        onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                    ) },
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

            } else {
                // Vista previa de la foto tomada
                PhotoPreview(viewModel)
            }
        } else {
            // Vista de permiso denegado
            PermissionDeniedView(navController)
        }

        // Botón de cerrar (siempre visible)
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

        if (state.errorMessage != null) {
            // Aquí puedes mostrar un Snackbar o un diálogo de error
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
        Text("¿QUIERES AGREGAR UNA NOTA?", fontSize = 16.sp, color = TextoBlanco)
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