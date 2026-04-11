package com.example.organizadoracademico.presentation.materia

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.presentation.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisMateriasScreen(
    navController: NavController,
    viewModel: MisMateriasViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)
    val cardDark = Color(0xFF1A1A2E)

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header con título y lupa
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "MIS MATERIAS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Barra de búsqueda
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar materia...", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorBase,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = cardDark.copy(alpha = 0.8f),
                        unfocusedContainerColor = cardDark.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de materias
            if (state.isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorBase)
                }
            } else {
                val materiasFiltradas = viewModel.getMateriasFiltradas(searchQuery)

                if (materiasFiltradas.isEmpty()) {
                    // Mensaje centrado SIN ícono
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay materias",
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Crea tu primera materia en 'Crear Horario'",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(materiasFiltradas) { index, materia ->
                            val delay = index * 50
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = tween(300, delayMillis = delay)
                                ) + slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = tween(300, delayMillis = delay)
                                )
                            ) {
                                SwipeToDeleteMateriaCard(
                                    materia = materia,
                                    totalImagenes = viewModel.getTotalImagenes(materia.id),
                                    ultimaFecha = viewModel.getUltimaFecha(materia.id),
                                    ultimasImagenes = viewModel.getUltimasImagenes(materia.id),
                                    onClick = {
                                        navController.navigate(Screen.Galeria.passMateriaId(materia.id))
                                    },
                                    onDelete = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Materia eliminada")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Navigation
            BottomNavigationBar5(
                onInicioClick = { navController.navigate(Screen.Main.route) },
                onHorarioClick = { navController.navigate(Screen.VerHorario.route) },
                onCrearClick = { navController.navigate(Screen.CrearHorario.route) },
                onMateriasClick = { /* Ya estamos en materias */ },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
fun SwipeToDeleteMateriaCard(
    materia: Materia,
    totalImagenes: Int,
    ultimaFecha: String,
    ultimasImagenes: List<Imagen>,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val deleteThreshold = 150.dp
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val color = when (materia.color) {
        "Morado" -> Color(0xFFB967FF)
        "Azul" -> Color(0xFF4361EE)
        "Verde" -> Color(0xFF52B788)
        "Naranja" -> Color(0xFFF48C06)
        "Rojo" -> Color(0xFFE83F6F)
        else -> Color(0xFFB967FF)
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Eliminar materia", color = Color.White) },
            text = { Text("¿Seguro que quieres eliminar ${materia.nombre}?", color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirmation = false
                }) {
                    Text("Eliminar", color = Color(0xFFEF4444))
                }
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
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX < -deleteThreshold.value) {
                            showDeleteConfirmation = true
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-deleteThreshold.value * 1.5f, 0f)
                    }
                )
            }
    ) {
        // Fondo rojo SOLO aparece al deslizar (no hay borde rojo fijo)
        if (offsetX < 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFFEF4444))
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = color.copy(alpha = 0.3f)
                )
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Materia",
                                tint = color,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = materia.nombre.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "$totalImagenes imágenes • $ultimaFecha",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver",
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (ultimasImagenes.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ultimasImagenes.take(6).forEach { imagen ->
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Imagen",
                                    tint = color.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (totalImagenes > 6) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${totalImagenes - 6}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin imágenes aún",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
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
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(Icons.Default.Home, "Inicio", onInicioClick)
            BottomNavItem(Icons.Default.CalendarToday, "Horario", onHorarioClick)
            BottomNavItem(Icons.Default.Add, "", onCrearClick, true)
            BottomNavItem(Icons.Default.Folder, "Materias", onMateriasClick)
            BottomNavItem(Icons.Default.Person, "Perfil", onPerfilClick)
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isCentral: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = if (isCentral) Modifier.size(56.dp) else Modifier
    ) {
        Box(
            modifier = if (isCentral) Modifier
                .background(Color(0xFF6681EA), CircleShape)
                .padding(12.dp)
            else Modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                label,
                tint = if (isCentral) Color.White else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(if (isCentral) 28.dp else 24.dp)
            )
        }
    }
}