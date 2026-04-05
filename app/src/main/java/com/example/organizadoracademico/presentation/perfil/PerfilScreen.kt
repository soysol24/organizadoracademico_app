package com.example.organizadoracademico.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejar mensajes de error con Snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            val result = snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "OK"
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.onEvent(PerfilEvent.ResetError)
            }
        }
    }

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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SuperficieCards,
                    contentColor = TextoBlanco,
                    actionColor = MoradoNeon,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
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
                // Contenido del perfil
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Foto de perfil
                    item {
                        ProfileHeader(usuario = state.usuario?.nombre ?: "Usuario")
                    }

                    // Estadísticas
                    item {
                        StatsCards(
                            materias = state.totalMaterias,
                            horarios = state.totalHorarios,
                            imagenes = state.totalImagenes
                        )
                    }

                    // Configuración
                    item {
                        ConfigurationSection(
                            vibracion = state.vibracionActivada,
                            sonido = state.sonidoActivado,
                            notificaciones = state.notificacionesActivadas,
                            onVibracionChange = {
                                viewModel.onEvent(PerfilEvent.ToggleVibracion(it))
                            },
                            onSonidoChange = {
                                viewModel.onEvent(PerfilEvent.ToggleSonido(it))
                            },
                            onNotificacionesChange = {
                                viewModel.onEvent(PerfilEvent.ToggleNotificaciones(it))
                            }
                        )
                    }

                    // Botón cerrar sesión
                    item {
                        LogoutButton(
                            onClick = { viewModel.onEvent(PerfilEvent.CerrarSesion) },
                            isLoading = state.isLoggingOut
                        )
                    }

                    // Espacio al final
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(usuario: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MoradoNeon.copy(alpha = 0.2f))
                .pulseEffect(true),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = usuario.uppercase(),
            fontSize = 24.sp,
            color = TextoBlanco,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "MIEMBRO DESDE: FEB 2026",
            fontSize = 12.sp,
            color = TextoGris,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun StatsCards(
    materias: Int,
    horarios: Int,
    imagenes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(valor = materias.toString(), label = "MATERIAS")
            StatItem(valor = horarios.toString(), label = "HORARIOS")
            StatItem(valor = imagenes.toString(), label = "FOTOS")
        }
    }
}

@Composable
fun StatItem(valor: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            fontSize = 24.sp,
            color = MoradoNeon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextoGris
        )
    }
}

@Composable
fun ConfigurationSection(
    vibracion: Boolean,
    sonido: Boolean,
    notificaciones: Boolean,
    onVibracionChange: (Boolean) -> Unit,
    onSonidoChange: (Boolean) -> Unit,
    onNotificacionesChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "⚙️ CONFIGURACIÓN",
                fontSize = 16.sp,
                color = MoradoNeon,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Opción Vibración
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📳",
                        fontSize = 20.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "Vibración",
                        fontSize = 16.sp,
                        color = TextoBlanco
                    )
                }
                Switch(
                    checked = vibracion,
                    onCheckedChange = onVibracionChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MoradoNeon,
                        checkedTrackColor = MoradoNeon.copy(alpha = 0.5f)
                    )
                )
            }

            Divider(
                color = BordeNeon,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Opción Sonido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔊",
                        fontSize = 20.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "Sonido",
                        fontSize = 16.sp,
                        color = TextoBlanco
                    )
                }
                Switch(
                    checked = sonido,
                    onCheckedChange = onSonidoChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MoradoNeon,
                        checkedTrackColor = MoradoNeon.copy(alpha = 0.5f)
                    )
                )
            }

            Divider(
                color = BordeNeon,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Opción Notificaciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 20.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "Notificaciones",
                        fontSize = 16.sp,
                        color = TextoBlanco
                    )
                }
                Switch(
                    checked = notificaciones,
                    onCheckedChange = onNotificacionesChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MoradoNeon,
                        checkedTrackColor = MoradoNeon.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun LogoutButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RosaNeon.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .pulseEffect(!isLoading)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = RosaNeon,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "📤 CERRAR SESIÓN",
                fontSize = 16.sp,
                color = RosaNeon
            )
        }
    }
}
