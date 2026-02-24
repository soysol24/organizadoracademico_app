package com.example.organizadoracademico.presentation.imagen.nota

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
import com.example.organizadoracademico.presentation.animation.pulseEffect
import com.example.organizadoracademico.presentation.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaScreen(
    navController: NavController,
    materiaId: Int,
    imageUri: String,
    viewModel: NotaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(NotaEvent.Inicializar(materiaId, imageUri))
    }

    // Navegar de regreso cuando se guarda
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            navController.popBackStack(
                route = "galeria/$materiaId",
                inclusive = false
            )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SuperficieCards
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Vista previa de la imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SuperficieCards),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📸",
                    fontSize = 64.sp
                )
                Text(
                    text = "Vista previa de la foto",
                    fontSize = 14.sp,
                    color = TextoGris,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de nota
            Text(
                text = "ESCRIBE UNA NOTA",
                fontSize = 14.sp,
                color = MoradoNeon,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.nota,
                onValueChange = { viewModel.onEvent(NotaEvent.NotaCambio(it)) },
                placeholder = {
                    Text(
                        "¿Qué aprendiste en esta clase?\nEj: Entendí el concepto de ViewModel...",
                        color = TextoGris
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SuperficieCards,
                    unfocusedContainerColor = SuperficieCards,
                    focusedBorderColor = MoradoNeon,
                    unfocusedBorderColor = BordeNeon,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            // Contador de caracteres
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${state.caracteresRestantes}/500",
                    fontSize = 12.sp,
                    color = if (state.caracteresRestantes < 50) RosaNeon else TextoGris
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de error
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Saltar
                OutlinedButton(
                    onClick = { viewModel.onEvent(NotaEvent.SaltarNota) },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextoGris
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("SALTAR")
                }

                // Botón Guardar
                Button(
                    onClick = { viewModel.onEvent(NotaEvent.GuardarNota) },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMatrix
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .pulseEffect(!state.isLoading)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = TextoBlanco,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("GUARDAR")
                    }
                }
            }
        }
    }
}