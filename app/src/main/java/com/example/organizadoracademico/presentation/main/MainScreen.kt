package com.example.organizadoracademico.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.animation.pulseEffect
import com.example.organizadoracademico.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

private val CardDark = Color(0xFF1A1A2E)
private val ColorBase = Color(0xFF6681EA)
private val ColorSecundario = Color(0xFF7E43AA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_offset"
    )

    val diaActual = obtenerDiaActualCompleto()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBase)
    ) {
        // Gradiente animado de fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            ColorSecundario.copy(alpha = 0.4f),
                            ColorSecundario.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        start = Offset(animatedOffset * 1500f, 0f),
                        end = Offset((animatedOffset + 0.5f) * 1500f, 1200f)
                    )
                )
        )

        // Partículas flotantes
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
                ),
                label = "particle_$index"
            )
            Box(
                modifier = Modifier
                    .offset(x = (40 + index * 100).dp, y = animatedY.dp)
                    .size((10 + index * 5).dp)
                    .background(Color.White.copy(alpha = 0.06f), CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // Header: solo lupa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Barra de búsqueda animada
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar...", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorBase,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = CardDark.copy(alpha = 0.8f),
                        unfocusedContainerColor = CardDark.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Saludo
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "¡Hola de nuevo!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.65f),
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = state.usuarioNombre.uppercase(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título sección
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HORARIO DE HOY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contenido con LazyColumn y peso para ocupar espacio
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else {
                    val horariosHoy = state.horariosHoy

                    if (horariosHoy.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No tienes clases programadas para hoy",
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Toca el botón '+' para agregar tu primer horario",
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.4f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        // Card del día
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.12f)
                                )
                            ) {
                                Text(
                                    text = diaActual.uppercase(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.2.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                )
                            }
                        }

                        items(horariosHoy) { clase ->
                            HorarioItem(
                                nombre = clase.nombre,
                                profesor = clase.profesor,
                                horaInicio = clase.horaInicio,
                                horaFin = clase.horaFin,
                                color = when (clase.color) {
                                    "Morado" -> Color(0xFFB967FF)
                                    "Azul"   -> Color(0xFF4361EE)
                                    "Verde"  -> Color(0xFF52B788)
                                    "Naranja"-> Color(0xFFF48C06)
                                    "Rojo"   -> Color(0xFFE83F6F)
                                    else     -> Color(0xFFB967FF)
                                }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Navigation (versión que sí funciona)
            BottomNavigationBar5(
                onInicioClick = { /* Ya estamos en inicio */ },
                onHorarioClick = { navController.navigate(Screen.VerHorario.route) },
                onCrearClick = { navController.navigate(Screen.CrearHorario.route) },
                onMateriasClick = { navController.navigate(Screen.MisMaterias.route) },
                onPerfilClick = { navController.navigate(Screen.Perfil.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HorarioItem(
    nombre: String,
    profesor: String,
    horaInicio: String,
    horaFin: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pulseEffect(true)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = color.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profesor,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "$horaInicio - $horaFin",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Inicio", onInicioClick)
            BottomNavItem(Icons.Default.CalendarToday, "Horario", onHorarioClick)
            // Botón central destacado
            IconButton(
                onClick = onCrearClick,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(ColorBase, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            BottomNavItem(Icons.Default.Folder, "Materias", onMateriasClick)
            BottomNavItem(Icons.Default.Person, "Perfil", onPerfilClick)
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )
    }
}

private fun obtenerDiaActualCompleto(): String {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY    -> "Lunes"
        Calendar.TUESDAY   -> "Martes"
        Calendar.WEDNESDAY -> "Miércoles"
        Calendar.THURSDAY  -> "Jueves"
        Calendar.FRIDAY    -> "Viernes"
        Calendar.SATURDAY  -> "Sábado"
        Calendar.SUNDAY    -> "Domingo"
        else               -> "Lunes"
    }
}