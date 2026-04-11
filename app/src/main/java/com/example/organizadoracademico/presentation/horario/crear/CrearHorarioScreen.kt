package com.example.organizadoracademico.presentation.horario.crear

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearHorarioScreen(
    navController: NavController,
    viewModel: CrearHorarioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val colorBase = Color(0xFF6681EA)
    val colorSecundario = Color(0xFF7E43AA)
    val neonGlow = colorSecundario.copy(alpha = 0.3f)
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

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.popBackStack()
        }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header con botón atrás
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
                    text = "ASIGNAR HORARIO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta glassmorphism
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = neonGlow,
                        spotColor = neonGlow
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardDark.copy(alpha = 0.9f)
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Materia
                    item {
                        DropdownFieldModern(
                            label = "MATERIA",
                            selectedValue = state.materiaSeleccionada?.nombre ?: "Seleccionar",
                            options = state.materias.map { it.nombre },
                            onOptionSelected = { index ->
                                state.materias.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarMateria(it))
                                }
                            },
                            icon = Icons.Default.Book
                        )
                    }

                    // Profesor
                    item {
                        DropdownFieldModern(
                            label = "PROFESOR",
                            selectedValue = state.profesorSeleccionado?.nombre ?: "Seleccionar",
                            options = state.profesores.map { it.nombre },
                            onOptionSelected = { index ->
                                state.profesores.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarProfesor(it))
                                }
                            },
                            icon = Icons.Default.Person
                        )
                    }

                    // Día
                    item {
                        DropdownFieldModern(
                            label = "DÍA",
                            selectedValue = state.diaSeleccionado,
                            options = state.dias,
                            onOptionSelected = { index ->
                                state.dias.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarDia(it))
                                }
                            },
                            icon = Icons.Default.CalendarToday
                        )
                    }

                    // Hora Inicio
                    item {
                        DropdownFieldModern(
                            label = "HORA INICIO",
                            selectedValue = state.horaInicio,
                            options = state.horas,
                            onOptionSelected = { index ->
                                state.horas.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarHoraInicio(it))
                                }
                            },
                            icon = Icons.Default.AccessTime
                        )
                    }

                    // Hora Fin
                    item {
                        DropdownFieldModern(
                            label = "HORA FIN",
                            selectedValue = state.horaFin,
                            options = state.horas,
                            onOptionSelected = { index ->
                                state.horas.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarHoraFin(it))
                                }
                            },
                            icon = Icons.Default.AccessTime
                        )
                    }

                    // Color
                    item {
                        DropdownFieldModern(
                            label = "COLOR",
                            selectedValue = state.colorSeleccionado,
                            options = state.colores,
                            onOptionSelected = { index ->
                                state.colores.getOrNull(index)?.let {
                                    viewModel.onEvent(CrearHorarioEvent.SeleccionarColor(it))
                                }
                            },
                            icon = Icons.Default.Palette
                        )
                    }

                    // Mensaje de error
                    if (state.errorMessage != null) {
                        item {
                            Text(
                                text = state.errorMessage!!,
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Botón Guardar
                    item {
                        Button(
                            onClick = { viewModel.onEvent(CrearHorarioEvent.GuardarHorario) },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = colorBase.copy(alpha = 0.3f)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(colorBase, colorSecundario)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = "AGREGAR HORARIO",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFieldModern(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6681EA),
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        // Campo original
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF6681EA)
                )
            },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Abrir",
                        tint = Color(0xFF6681EA)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A2A3E),
                unfocusedContainerColor = Color(0xFF2A2A3E),
                focusedBorderColor = Color(0xFF6681EA),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown con altura máxima y scroll (carrete)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(240.dp)
                .heightIn(max = 250.dp)  // ← ALTURA MÁXIMA (carrete)
                .background(
                    color = Color(0xFF1E1E2E),
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f)
                )
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = option == selectedValue

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = if (isSelected) Color(0xFF6681EA) else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    },
                    onClick = {
                        onOptionSelected(index)
                        expanded = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .then(
                            if (isSelected) Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF6681EA),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                            else Modifier
                        )
                )

                if (index < options.size - 1) {
                    Divider(
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}