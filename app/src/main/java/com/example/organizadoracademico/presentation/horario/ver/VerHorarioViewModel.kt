package com.example.organizadoracademico.presentation.horario.ver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.usercase.horario.DeleteHorarioUseCase
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.profesor.GetProfesoresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerHorarioViewModel(
    private val getHorariosUseCase: GetHorariosUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getProfesoresUseCase: GetProfesoresUseCase,
    private val deleteHorarioUseCase: DeleteHorarioUseCase,
    private val sessionManager: SessionManager // <--- AGREGADO: Para obtener el ID real
) : ViewModel() {

    private val _state = MutableStateFlow(VerHorarioState())
    val state: StateFlow<VerHorarioState> = _state.asStateFlow()

    // Obtenemos el ID del usuario logueado
    private val userId = sessionManager.getUserId()

    init {
        cargarDatos()
    }

    fun onEvent(event: VerHorarioEvent) {
        when (event) {
            is VerHorarioEvent.CargarHorarios -> cargarDatos()
            is VerHorarioEvent.SeleccionarDia -> {
                _state.update { it.copy(diaSeleccionado = event.dia) }
            }
            is VerHorarioEvent.EliminarHorario -> eliminarHorario(event.horarioId)
        }
    }

    fun refresh() {
        cargarDatos()
    }

    private fun cargarDatos() {
        _state.update { it.copy(isLoading = true) }

        combine(
            getHorariosUseCase(userId),
            getMateriasUseCase(),
            getProfesoresUseCase()
        ) { horarios, materias, profesores ->
            Triple(horarios, materias, profesores)
        }
            .distinctUntilChanged()
            .onEach { triple ->
                val horarios = triple.first
                val materias = triple.second
                val profesores = triple.third
                _state.update {
                    it.copy(
                        horarios = horarios,
                        materias = materias.associateBy { m -> m.id },
                        profesores = profesores.associateBy { p -> p.id },
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Error al cargar datos: ${e.message}")
                }
            }
            .launchIn(viewModelScope)
    }

    private fun eliminarHorario(horarioId: Int) {
        viewModelScope.launch {
            val result = deleteHorarioUseCase.invoke(horarioId)
            result.onSuccess {
                // No hace falta llamar a cargarDatos() manualmente si usas flujos (Flow)
                // ya que Room notificará automáticamente el cambio.
            }.onFailure { exception ->
                _state.update {
                    it.copy(errorMessage = exception.message ?: "Error al eliminar")
                }
            }
        }
    }

    // --- Funciones de utilidad para la UI ---

    fun getHorariosPorDia(dia: String): List<Horario> {
        val diaCompleto = when (dia) {
            "LUN" -> "Lunes"
            "MAR" -> "Martes"
            "MIÉ" -> "Miércoles"
            "JUE" -> "Jueves"
            "VIE" -> "Viernes"
            "SÁB" -> "Sábado"
            "DOM" -> "Domingo"
            else -> dia
        }
        return _state.value.horarios.filter { it.dia == diaCompleto }
            .sortedBy { it.horaInicio }
    }

    fun getNombreMateria(materiaId: Int): String =
        _state.value.materias[materiaId]?.nombre ?: "Desconocida"

    fun getNombreProfesor(profesorId: Int): String =
        _state.value.profesores[profesorId]?.nombre ?: "Desconocido"

    fun getColorMateria(materiaId: Int): String =
        _state.value.materias[materiaId]?.color ?: "Morado"

    fun getIconoMateria(materiaId: Int): String {
        return when (getNombreMateria(materiaId)) {
            "Programación Móvil" -> "📱"
            "Bases de Datos" -> "🗄️"
            "Estructuras de Datos" -> "🌲"
            "Programación Web" -> "💻"
            "Sistemas Operativos" -> "🔧"
            "Ingeniería de Software" -> "📊"
            "Redes" -> "🌐"
            else -> "📚"
        }
    }
}