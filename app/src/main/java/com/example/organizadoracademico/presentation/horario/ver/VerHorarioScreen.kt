package com.example.organizadoracademico.presentation.horario.ver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun VerHorarioScreen(
    navController: NavController,
    viewModel: VerHorarioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MI HORARIO", color = TextoBlanco) },
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
        ) {
            // Selector de días
            DiasSemanaRow(
                dias = state.diasSemana,
                diaSeleccionado = state.diaSeleccionado,
                onDiaClick = { dia ->
                    viewModel.onEvent(VerHorarioEvent.SeleccionarDia(dia))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Línea decorativa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BordeNeon)
            )

            // Lista de horarios
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MoradoNeon)
                }
            } else {
                HorariosPorDia(
                    viewModel = viewModel,
                    dia = state.diaSeleccionado
                )
            }
        }
    }
}

@Composable
fun DiasSemanaRow(
    dias: List<String>,
    diaSeleccionado: String,
    onDiaClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dias.forEach { dia ->
            val isSelected = dia == diaSeleccionado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MoradoNeon else SuperficieCards)
                    .clickable { onDiaClick(dia) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = dia,
                    color = if (isSelected) TextoBlanco else TextoGris,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HorariosPorDia(
    viewModel: VerHorarioViewModel,
    dia: String
) {
    val horarios = viewModel.getHorariosPorDia(dia)

    if (horarios.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay clases este día",
                color = TextoGris,
                fontSize = 16.sp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título del día
            item {
                Text(
                    text = "⚡ $dia",
                    fontSize = 24.sp,
                    color = MoradoNeon,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Horarios del día
            items(horarios) { horario ->
                HorarioCard(
                    horario = horario,
                    viewModel = viewModel,
                    onEliminar = {
                        viewModel.onEvent(VerHorarioEvent.EliminarHorario(horario.id))
                    }
                )
            }

            // Espacio al final
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HorarioCard(
    horario: com.example.organizadoracademico.domain.model.Horario,
    viewModel: VerHorarioViewModel,
    onEliminar: () -> Unit
) {
    val materiaNombre = viewModel.getNombreMateria(horario.materiaId)
    val profesorNombre = viewModel.getNombreProfesor(horario.profesorId)
    val icono = viewModel.getIconoMateria(horario.materiaId)
    val color = when (viewModel.getColorMateria(horario.materiaId)) {
        "Morado" -> MoradoNeon
        "Azul" -> AzulElectrico
        "Verde" -> VerdeMatrix
        "Naranja" -> NaranjaNeon
        "Rojo" -> RosaNeon
        else -> MoradoNeon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pulseEffect(true),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuperficieCards
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (horario.pendienteSync) {
                Text(
                    text = "Pendiente de subir",
                    fontSize = 12.sp,
                    color = TextoGris,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono y materia
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icono,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = materiaNombre.uppercase(),
                            fontSize = 16.sp,
                            color = TextoBlanco,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = profesorNombre,
                            fontSize = 14.sp,
                            color = TextoGris
                        )
                    }
                }

                // Horario
                Text(
                    text = horario.horaInicio,
                    fontSize = 14.sp,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEliminar,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.pulseEffect(true)
                ) {
                    Text(
                        text = "ELIMINAR",
                        fontSize = 12.sp,
                        color = color
                    )
                }
            }
        }
    }
}
