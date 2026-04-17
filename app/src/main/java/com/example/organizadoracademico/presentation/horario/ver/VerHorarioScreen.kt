package com.example.organizadoracademico.presentation.horario.ver

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


private val cardDark = Color(0xFF161B2E)

@Composable
fun VerHorarioScreen(
    navController: NavController,
    viewModel: VerHorarioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "gradient"
    )

    val diasAbreviados = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE")
    val diasCompletos = mapOf(
        "LUN" to "Lunes",
        "MAR" to "Martes",
        "MIÉ" to "Miércoles",
        "JUE" to "Jueves",
        "VIE" to "Viernes"
    )

    Box(Modifier.fillMaxSize().background(colorBase)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    listOf(
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

        repeat(2) { index ->
            val animatedY by infiniteTransition.animateFloat(
                initialValue = (-100).dp.value,
                targetValue = 1200.dp.value,
                animationSpec = infiniteRepeatable(
                    tween(15000 + index * 2000, easing = LinearEasing),
                    RepeatMode.Restart
                ),
                label = "float$index"
            )
            Box(
                Modifier
                    .offset(x = (100 + index * 200).dp, y = animatedY.dp)
                    .size((15 + index * 5).dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            )
        }

        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Text("MI HORARIO", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                diasAbreviados.forEach { dia ->
                    val selected = dia == state.diaSeleccionado
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) Color.White.copy(alpha = 0.12f) else Color.Transparent)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .clickable { viewModel.onEvent(VerHorarioEvent.SeleccionarDia(dia)) }
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(dia, color = Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            val diaCompleto = diasCompletos[state.diaSeleccionado] ?: "Lunes"
            Text(
                diaCompleto.uppercase(),
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(18.dp))

            if (state.isLoading) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                val horarios = viewModel.getHorariosPorDia(diaCompleto)
                if (horarios.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay clases este día",
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF20243A).copy(alpha = 0.78f)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(horarios) { horario ->
                                SwipeToDeleteCard(horario, viewModel) {
                                    viewModel.onEvent(VerHorarioEvent.EliminarHorario(horario.id))
                                    scope.launch { snackbarHostState.showSnackbar("Horario eliminado") }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            BottomNavigationBar5(
                onInicioClick = { navController.navigate(Screen.Main.route) },
                onHorarioClick = {},
                onCrearClick = { navController.navigate(Screen.CrearHorario.route) },
                onMateriasClick = { navController.navigate(Screen.MisMaterias.route) },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }
    }
}

@Composable
fun SwipeToDeleteCard(
    horario: com.example.organizadoracademico.domain.model.Horario,
    viewModel: VerHorarioViewModel,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    val materiaNombre = viewModel.getNombreMateria(horario.materiaId)
    val profesorNombre = viewModel.getNombreProfesor(horario.profesorId)

    // CORRECCIÓN: Función mejorada para obtener el color
    val color = remember(horario.materiaId) {
        val colorNombre = viewModel.getColorMateria(horario.materiaId)
            .trim()
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")

        when (colorNombre) {
            "morado", "purple", "violeta" -> Color(0xFFB967FF)
            "azul", "blue" -> Color(0xFF4D96FF)
            "verde", "green" -> Color(0xFF52B788)
            "naranja", "orange" -> Color(0xFFF48C06)
            "rojo", "red" -> Color(0xFFE63946)
            else -> Color(0xFF52B788) // Color por defecto verde
        }
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar horario", color = Color.White) },
            text = { Text("¿Seguro que quieres eliminar ${materiaNombre}?", color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirmation = false
                }) { Text("Eliminar", color = Color(0xFFEF4444)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar", color = Color(0xFF6681EA))

                }
            },
            containerColor = Color(0xFF1A1A2E),
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        Modifier.fillMaxWidth().pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (offsetX < -150f) showDeleteConfirmation = true
                    offsetX = 0f
                },
                onHorizontalDrag = { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(-220f, 0f)
                }
            )
        }
    ) {
        Box(
            Modifier.matchParentSize().clip(RoundedCornerShape(18.dp)).background(Color(0xFFEF4444)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(Icons.Default.Delete, null, tint = Color.White, modifier = Modifier.padding(end = 20.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardDark),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.School,
                        null,
                        tint = color,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        // CORRECCIÓN: Texto sin negrita y sin uppercase forzado
                        Text(
                            text = materiaNombre,  // Quitamos el .uppercase()
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Normal,  // Cambiado de SemiBold a Normal
                            fontSize = 16.sp,
                            letterSpacing = 0.2.sp  // Reducido el espaciado
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = profesorNombre,
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 13.sp,  // Reducido ligeramente
                            fontWeight = FontWeight.Normal  // Aseguramos que no esté en negrita
                        )
                    }
                }
                Text(
                    text = "${horario.horaInicio} - ${horario.horaFin}",
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar5(
    onInicioClick: () -> Unit,
    onHorarioClick: () -> Unit,
    onCrearClick: () -> Unit,
    onMateriasClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, onInicioClick)
            BottomNavItem(Icons.Default.CalendarToday, onHorarioClick)
            BottomNavItem(Icons.Default.Add, onCrearClick, true)
            BottomNavItem(Icons.Default.Folder, onMateriasClick)
            BottomNavItem(Icons.Default.Person, onPerfilClick)
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isCentral: Boolean = false
) {
    IconButton(onClick = onClick) {
        Box(
            modifier = if (isCentral) Modifier.background(Color(0xFF6681EA), RoundedCornerShape(16.dp)).padding(12.dp) else Modifier
        ) {
            Icon(icon, null, tint = Color.White)
        }
    }
}