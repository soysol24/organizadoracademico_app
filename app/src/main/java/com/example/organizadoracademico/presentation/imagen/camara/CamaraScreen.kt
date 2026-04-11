package com.example.organizadoracademico.presentation.imagen.camara

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.organizadoracademico.hardware.camera.CameraManager
import com.example.organizadoracademico.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen(
    navController: NavController,
    materiaId: Int,
    viewModel: CamaraViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cameraManager = remember { CameraManager(context) }
    var mostrarDialogoDescartar by remember { mutableStateOf(false) }

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

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(CamaraEvent.LimpiarTodo)
        viewModel.onEvent(CamaraEvent.Inicializar(materiaId))
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.photoSaved) {
        if (state.photoSaved && !state.lastPhotoUri.isNullOrBlank()) {
            try {
                val encodedUri = URLEncoder.encode(state.lastPhotoUri, StandardCharsets.UTF_8.toString())
                navController.navigate(Screen.Nota.passParams(materiaId, encodedUri))
                viewModel.onEvent(CamaraEvent.ResetNavegacion)
            } catch (e: Exception) {
                viewModel.onEvent(CamaraEvent.ResetError)
                viewModel.onEvent(CamaraEvent.ResetNavegacion)
            }
        }
    }

    // Diálogo de confirmación para descartar foto
    if (mostrarDialogoDescartar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDescartar = false },
            title = {
                Text(
                    text = "Descartar foto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres descartar esta foto? Se perderá.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoDescartar = false
                        viewModel.onEvent(CamaraEvent.DescartarFoto)
                    }
                ) {
                    Text(
                        text = "Descartar",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoDescartar = false }) {
                    Text(
                        text = "Cancelar",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color(0xFF1A1A2E),
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

        // Botón de cerrar
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White
            )
        }

        if (hasPermission) {
            if (state.lastPhotoUri != null) {
                PhotoPreviewModern(viewModel, onDescartarClick = { mostrarDialogoDescartar = true })
            } else {
                cameraManager.CameraPreview(
                    onImageCaptured = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                    onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                )

                // Botón para tomar foto
                Button(
                    onClick = {
                        cameraManager.takePhoto(
                            onSuccess = { uri -> viewModel.onEvent(CamaraEvent.FotoTomada(uri)) },
                            onError = { viewModel.onEvent(CamaraEvent.ResetError) }
                        )
                    },
                    enabled = !state.isTakingPhoto,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6681EA)
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Tomar foto",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } else {
            PermissionDeniedViewModern(navController)
        }
    }
}

@Composable
fun PhotoPreviewModern(
    viewModel: CamaraViewModel,
    onDescartarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(250.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF6681EA).copy(alpha = 0.3f)
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
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Vista previa",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¿QUIERES GUARDAR ESTA FOTO?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onDescartarClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF6B6B) // ROSITA
                )
            ) {
                Text("DESCARTAR", fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { viewModel.onEvent(CamaraEvent.ContinuarConNota) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A4A5A), // Fondo gris
                    contentColor = Color(0xFF52B788) // Letras VERDES
                )
            ) {
                Text("CONTINUAR", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun PermissionDeniedViewModern(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = "Cámara",
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "PERMISO DE CÁMARA DENEGADO",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Necesitas conceder el permiso para usar la cámara",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6681EA)
            )
        ) {
            Text("VOLVER")
        }
    }
}