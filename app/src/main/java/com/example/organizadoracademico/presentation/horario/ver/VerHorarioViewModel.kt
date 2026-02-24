package com.example.organizadoracademico.presentation.horario.ver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.model.Profesor
import com.example.organizadoracademico.domain.usercase.horario.DeleteHorarioUseCase
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.profesor.GetProfesoresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerHorarioViewModel(
    private val getHorariosUseCase: GetHorariosUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getProfesoresUseCase: GetProfesoresUseCase,
    private val deleteHorarioUseCase: DeleteHorarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VerHorarioState())
    val state: StateFlow<VerHorarioState> = _state.asStateFlow()

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

    private fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Cargar horarios
                getHorariosUseCase().collect { horarios ->

                    // Cargar materias para mapear IDs a nombres
                    getMateriasUseCase().collect { materiasList ->
                        val materiasMap = materiasList.associateBy { it.id }

                        // Cargar profesores para mapear IDs a nombres
                        getProfesoresUseCase().collect { profesoresList ->
                            val profesoresMap = profesoresList.associateBy { it.id }

                            _state.update {
                                it.copy(
                                    horarios = horarios,
                                    materias = materiasMap,
                                    profesores = profesoresMap,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar horarios: ${e.message}"
                    )
                }
            }
        }
    }

    private fun eliminarHorario(horarioId: Int) {
        viewModelScope.launch {
            val result = deleteHorarioUseCase.invoke(horarioId)

            result.onSuccess {
                // Recargar horarios después de eliminar
                cargarDatos()
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        errorMessage = exception.message ?: "Error al eliminar horario"
                    )
                }
            }
        }
    }

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

    fun getNombreMateria(materiaId: Int): String {
        return _state.value.materias[materiaId]?.nombre ?: "Desconocida"
    }

    fun getNombreProfesor(profesorId: Int): String {
        return _state.value.profesores[profesorId]?.nombre ?: "Desconocido"
    }

    fun getColorMateria(materiaId: Int): String {
        return _state.value.materias[materiaId]?.color ?: "Morado"
    }

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