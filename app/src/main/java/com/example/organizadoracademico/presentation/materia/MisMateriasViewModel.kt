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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat

class MisMateriasViewModel(
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MisMateriasState())
    val state: StateFlow<MisMateriasState> = _state.asStateFlow()

    init {
        cargarDatos()
    }

    fun onEvent(event: MisMateriasEvent) {
        when (event) {
            is MisMateriasEvent.CargarMaterias -> cargarDatos()
            is MisMateriasEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is MisMateriasEvent.SeleccionarMateria -> {
                // La navegación se maneja en la UI
            }
        }
    }

    private fun cargarDatos() {
        _state.update { it.copy(isLoading = true) }

        getMateriasUseCase()
            .flatMapLatest { materias ->
                if (materias.isEmpty()) {
                    flow { emit(Pair(emptyList<Materia>(), emptyMap<Int, List<Imagen>>())) }
                } else {
                    val imageFlows = materias.map {
                        getImagenesPorMateriaUseCase(it.id)
                    }
                    combine(imageFlows) { imagesArray ->
                        val imagesMap = materias.zip(imagesArray.toList()).associate {
                            (materia, images) -> materia.id to images
                        }
                        Pair(materias, imagesMap)
                    }
                }
            }
            .onEach { (materias, imagesMap) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        materias = materias,
                        imagenesPorMateria = imagesMap
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                }
            }
            .launchIn(viewModelScope)
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