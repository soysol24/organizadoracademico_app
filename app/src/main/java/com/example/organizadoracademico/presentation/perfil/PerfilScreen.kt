package com.example.organizadoracademico.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.organizadoracademico.presentation.theme.*
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar al login cuando cierra sesión
    LaunchedEffect(state.isLoggingOut) {
        if (state.isLoggingOut == false && state.usuario == null) {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PERFIL", color = TextoBlanco) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MoradoNeon)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Foto de perfil
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(MoradoNeon.copy(alpha = 0.2f))
                            .pulseEffect(true),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 60.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nombre
                    Text(
                        text = state.usuario?.nombre?.uppercase() ?: "USUARIO",
                        fontSize = 24.sp,
                        color = TextoBlanco,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // Email
                    Text(
                        text = state.usuario?.email ?: "correo@ejemplo.com",
                        fontSize = 16.sp,
                        color = TextoGris,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Botón Cerrar Sesión
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pulseEffect(true)
                            .clickable { viewModel.onEvent(PerfilEvent.CerrarSesion) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SuperficieCards
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RosaNeon.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📤",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "CERRAR SESIÓN",
                                fontSize = 16.sp,
                                color = RosaNeon,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (state.isLoggingOut) {
                                CircularProgressIndicator(
                                    color = RosaNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "▶",
                                    fontSize = 16.sp,
                                    color = RosaNeon
                                )
                            }
                        }
                    }
                }
            }

            // Mensaje de error
            if (state.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(
                            onClick = { viewModel.onEvent(PerfilEvent.ResetError) }
                        ) {
                            Text("OK", color = MoradoNeon)
                        }
                    },
                    containerColor = SuperficieCards,
                    contentColor = TextoBlanco
                ) {
                    Text(state.errorMessage!!)
                }
            }
        }
    }
}