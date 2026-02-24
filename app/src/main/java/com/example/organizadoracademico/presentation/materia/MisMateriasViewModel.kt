package com.example.organizadoracademico.presentation.materia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class MisMateriasViewModel(
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MisMateriasState())
    val state: StateFlow<MisMateriasState> = _state.asStateFlow()

    private val imagenesMap = mutableMapOf<Int, List<Imagen>>()

    init {
        cargarMaterias()
    }

    fun onEvent(event: MisMateriasEvent) {
        when (event) {
            is MisMateriasEvent.CargarMaterias -> cargarMaterias()
            is MisMateriasEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is MisMateriasEvent.SeleccionarMateria -> {
                // La navegación se maneja en la UI
            }
        }
    }

    private fun cargarMaterias() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                getMateriasUseCase().collect { materias ->
                    _state.update {
                        it.copy(
                            materias = materias,
                            isLoading = false
                        )
                    }

                    // Cargar imágenes para cada materia
                    materias.forEach { materia ->
                        cargarImagenesPorMateria(materia.id)
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar materias: ${e.message}"
                    )
                }
            }
        }
    }

    private fun cargarImagenesPorMateria(materiaId: Int) {
        viewModelScope.launch {
            try {
                getImagenesPorMateriaUseCase(materiaId).collect { imagenes ->
                    imagenesMap[materiaId] = imagenes
                    _state.update {
                        it.copy(imagenesPorMateria = imagenesMap.toMap())
                    }
                }
            } catch (e: Exception) {
                // Silencioso, no mostrar error por ahora
            }
        }
    }

    fun getMateriasFiltradas(): List<Materia> {
        val query = _state.value.searchQuery.lowercase()
        return if (query.isEmpty()) {
            _state.value.materias
        } else {
            _state.value.materias.filter {
                it.nombre.lowercase().contains(query)
            }
        }
    }

    fun getUltimasImagenes(materiaId: Int, limite: Int = 6): List<Imagen> {
        return _state.value.imagenesPorMateria[materiaId]?.take(limite) ?: emptyList()
    }

    fun getTotalImagenes(materiaId: Int): Int {
        return _state.value.imagenesPorMateria[materiaId]?.size ?: 0
    }

    fun getUltimaFecha(materiaId: Int): String {
        val imagenes = _state.value.imagenesPorMateria[materiaId]
        return if (imagenes.isNullOrEmpty()) {
            "Sin imágenes"
        } else {
            val ultima = imagenes.maxByOrNull { it.fecha }
            val fecha = SimpleDateFormat("dd/MM/yy").format(ultima?.fecha ?: 0)
            "Última: $fecha"
        }
    }
}