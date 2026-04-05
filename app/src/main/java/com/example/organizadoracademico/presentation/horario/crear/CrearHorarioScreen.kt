package com.example.organizadoracademico.presentation.horario.crear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.theme.*
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearHorarioScreen(
    navController: NavController,
    viewModel: CrearHorarioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar back cuando se guarda exitosamente
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ASIGNAR HORARIO", color = TextoBlanco) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPrincipal)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Materia
            item {
                DropdownField(
                    label = "MATERIA",
                    selectedValue = state.materiaSeleccionada?.nombre ?: "Seleccionar",
                    options = state.materias.map { it.nombre },
                    onOptionSelected = { index ->
                        state.materias.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarMateria(it))
                        }
                    }
                )
            }

            // Profesor
            item {
                DropdownField(
                    label = "PROFESOR",
                    selectedValue = state.profesorSeleccionado?.nombre ?: "Seleccionar",
                    options = state.profesores.map { it.nombre },
                    onOptionSelected = { index ->
                        state.profesores.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarProfesor(it))
                        }
                    }
                )
            }

            // Día
            item {
                DropdownField(
                    label = "DÍA",
                    selectedValue = state.diaSeleccionado,
                    options = state.dias,
                    onOptionSelected = { index ->
                        state.dias.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarDia(it))
                        }
                    }
                )
            }

            // Hora Inicio
            item {
                DropdownField(
                    label = "HORA INICIO",
                    selectedValue = state.horaInicio,
                    options = state.horas,
                    onOptionSelected = { index ->
                        state.horas.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarHoraInicio(it))
                        }
                    }
                )
            }

            // Hora Fin
            item {
                DropdownField(
                    label = "HORA FIN",
                    selectedValue = state.horaFin,
                    options = state.horas,
                    onOptionSelected = { index ->
                        state.horas.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarHoraFin(it))
                        }
                    }
                )
            }

            // Color
            item {
                DropdownField(
                    label = "COLOR",
                    selectedValue = state.colorSeleccionado,
                    options = state.colores,
                    onOptionSelected = { index ->
                        state.colores.getOrNull(index)?.let {
                            viewModel.onEvent(CrearHorarioEvent.SeleccionarColor(it))
                        }
                    }
                )
            }

            // Mensaje de error
            if (state.errorMessage != null) {
                item {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoNeon
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .pulseEffect(!state.isLoading)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = TextoBlanco,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "📳 AGREGAR HORARIO",
                            fontSize = 16.sp,
                            color = TextoBlanco
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MoradoNeon,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Text("▼", color = MoradoNeon)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SuperficieCards,
                    unfocusedContainerColor = SuperficieCards,
                    focusedBorderColor = MoradoNeon,
                    unfocusedBorderColor = TextoGris,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SuperficieCards)
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextoBlanco) },
                        onClick = {
                            onOptionSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
