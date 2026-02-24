package com.example.organizadoracademico.presentation.materia

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
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.*
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisMateriasScreen(
    navController: NavController,
    viewModel: MisMateriasViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val materiasFiltradas = viewModel.getMateriasFiltradas()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MIS MATERIAS", color = TextoBlanco) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 24.sp, color = TextoBlanco)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Agregar materia */ }) {
                        Text("➕", fontSize = 20.sp, color = MoradoNeon)
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
            // Barra de búsqueda
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(MisMateriasEvent.SearchQueryChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de materias
            if (state.isLoading && materiasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MoradoNeon)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(materiasFiltradas, key = { it.id }) { materia ->
                        MateriaCard(
                            materia = materia,
                            totalImagenes = viewModel.getTotalImagenes(materia.id),
                            ultimaFecha = viewModel.getUltimaFecha(materia.id),
                            ultimasImagenes = viewModel.getUltimasImagenes(materia.id),
                            onClick = {
                                navController.navigate(Screen.Galeria.passMateriaId(materia.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("🔍 Buscar materia...", color = TextoGris) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MateriaCard(
    materia: Materia,
    totalImagenes: Int,
    ultimaFecha: String,
    ultimasImagenes: List<com.example.organizadoracademico.domain.model.Imagen>,
    onClick: () -> Unit
) {
    val color = when (materia.color) {
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
            .pulseEffect(true)
            .clickable { onClick() },
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
            // Header de la materia
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = materia.icono ?: "📚",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = materia.nombre.uppercase(),
                            fontSize = 16.sp,
                            color = TextoBlanco,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$totalImagenes imágenes • $ultimaFecha",
                            fontSize = 12.sp,
                            color = TextoGris
                        )
                    }
                }

                Text(
                    text = "▶",
                    fontSize = 16.sp,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Miniaturas de últimas imágenes
            if (ultimasImagenes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ultimasImagenes.take(6).forEach { _ ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📸",
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (totalImagenes > 6) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${totalImagenes - 6}",
                                fontSize = 12.sp,
                                color = color
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin imágenes aún",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }
            }
        }
    }
}
