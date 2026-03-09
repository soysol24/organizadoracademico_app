package com.example.organizadoracademico.presentation.materia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class MisMateriasViewModel(
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(MisMateriasState())
    val state: StateFlow<MisMateriasState> = _state.asStateFlow()

    // El ID del usuario logueado lo usaremos solo para las imágenes
    private val userId = sessionManager.getUserId()

    init {
        cargarDatos()
    }

    fun onEvent(event: MisMateriasEvent) {
        when (event) {
            is MisMateriasEvent.CargarMaterias -> cargarDatos()
            is MisMateriasEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is MisMateriasEvent.SeleccionarMateria -> { /* Navegación */ }
        }
    }

    private fun cargarDatos() {
        _state.update { it.copy(isLoading = true) }

        // CAMBIO 1: getMateriasUseCase() ya no pide userId (es global)
        getMateriasUseCase()
            .flatMapLatest { materias ->
                if (materias.isEmpty()) {
                    flowOf(Pair(emptyList<Materia>(), emptyMap<Int, List<Imagen>>()))
                } else {
                    val imageFlows = materias.map { materia ->
                        // CAMBIO 2: Las imágenes SÍ piden materiaId Y userId (son privadas)
                        getImagenesPorMateriaUseCase(materia.id, userId).map { imagenes ->
                            materia.id to imagenes
                        }
                    }
                    combine(imageFlows) { results ->
                        Pair(materias, results.toMap())
                    }
                }
            }
            .onEach { (materias, imagesMap) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        materias = materias,
                        imagenesPorMateria = imagesMap,
                        errorMessage = null
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // ... (resto de funciones de filtrado y fecha se mantienen igual)

    fun getMateriasFiltradas(): List<Materia> {
        val query = _state.value.searchQuery.lowercase().trim()
        return if (query.isEmpty()) _state.value.materias
        else _state.value.materias.filter { it.nombre.lowercase().contains(query) }
    }

    fun getUltimasImagenes(materiaId: Int, limite: Int = 6): List<Imagen> =
        _state.value.imagenesPorMateria[materiaId]?.take(limite) ?: emptyList()

    fun getTotalImagenes(materiaId: Int): Int = _state.value.imagenesPorMateria[materiaId]?.size ?: 0

    fun getUltimaFecha(materiaId: Int): String {
        val imagenes = _state.value.imagenesPorMateria[materiaId]
        return if (imagenes.isNullOrEmpty()) "Sin imágenes"
        else {
            val ultima = imagenes.maxByOrNull { it.fecha }
            val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            "Última: ${formatter.format(Date(ultima?.fecha ?: 0))}"
        }
    }
}