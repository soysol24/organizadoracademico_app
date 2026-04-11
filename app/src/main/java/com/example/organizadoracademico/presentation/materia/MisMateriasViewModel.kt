package com.example.organizadoracademico.presentation.materia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase // IMPORTANTE
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class MisMateriasViewModel(
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getHorariosUseCase: GetHorariosUseCase, // Agregado
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(MisMateriasState())
    val state: StateFlow<MisMateriasState> = _state.asStateFlow()

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

        // 1. Combinamos las materias globales con los horarios del usuario
        combine(
            getMateriasUseCase(),
            getHorariosUseCase(userId)
        ) { todasLasMaterias, misHorarios ->
            // Filtramos: Solo nos quedamos con las materias cuyo ID esté en mis horarios
            val idsInscritos = misHorarios.map { it.materiaId }.toSet()
            todasLasMaterias.filter { it.id in idsInscritos }
        }
            .flatMapLatest { misMateriasInscritas ->
                if (misMateriasInscritas.isEmpty()) {
                    flowOf(Pair(emptyList<Materia>(), emptyMap<Int, List<Imagen>>()))
                } else {
                    // 2. Por cada materia inscrita, traemos sus imágenes privadas
                    val imageFlows = misMateriasInscritas.map { materia ->
                        getImagenesPorMateriaUseCase(materia.id, userId).map { imagenes ->
                            materia.id to imagenes
                        }
                    }
                    combine(imageFlows) { results ->
                        Pair(misMateriasInscritas, results.toMap())
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
        fun getMateriasFiltradas(query: String): List<Materia> {
            val materias = state.value.materias
            return if (query.isEmpty()) {
                materias
            } else {
                materias.filter { it.nombre.contains(query, ignoreCase = true) }
            }
        }
    }
    fun getMateriasFiltradas(query: String): List<Materia> {
        val materias = state.value.materias
        return if (query.isEmpty()) {
            materias
        } else {
            materias.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }
}
