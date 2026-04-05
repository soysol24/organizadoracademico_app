package com.example.organizadoracademico.presentation.horario.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.usercase.horario.AddHorarioUseCase
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.profesor.GetProfesoresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearHorarioViewModel(
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getProfesoresUseCase: GetProfesoresUseCase,
    private val getHorariosUseCase: GetHorariosUseCase,
    private val addHorarioUseCase: AddHorarioUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(CrearHorarioState())
    val state: StateFlow<CrearHorarioState> = _state.asStateFlow()

    private val userId = sessionManager.getUserId()
    private var horariosExistentes: List<Horario> = emptyList()

    init {
        cargarDatosIniciales()
    }

    // ... (onEvent se mantiene igual)
    fun onEvent(event: CrearHorarioEvent) {
        when (event) {
            is CrearHorarioEvent.CargarDatosIniciales -> cargarDatosIniciales()
            is CrearHorarioEvent.SeleccionarMateria -> {
                _state.update { it.copy(materiaSeleccionada = event.materia) }
            }
            is CrearHorarioEvent.SeleccionarProfesor -> {
                _state.update { it.copy(profesorSeleccionado = event.profesor) }
            }
            is CrearHorarioEvent.SeleccionarDia -> {
                _state.update { it.copy(diaSeleccionado = event.dia) }
            }
            is CrearHorarioEvent.SeleccionarHoraInicio -> {
                _state.update { it.copy(horaInicio = event.hora) }
            }
            is CrearHorarioEvent.SeleccionarHoraFin -> {
                _state.update { it.copy(horaFin = event.hora) }
            }
            is CrearHorarioEvent.SeleccionarColor -> {
                _state.update { it.copy(colorSeleccionado = event.color) }
            }
            is CrearHorarioEvent.GuardarHorario -> guardarHorario()
            is CrearHorarioEvent.ResetSuccess -> {
                _state.update { it.copy(isSuccess = false) }
            }
        }
    }

    private fun cargarDatosIniciales() {
        _state.update { it.copy(isLoading = true) }

        val materiasFlow = getMateriasUseCase()
        val profesoresFlow = getProfesoresUseCase()
        val horariosFlow = getHorariosUseCase(userId)

        combine(materiasFlow, profesoresFlow, horariosFlow) { materias, profesores, horarios ->
            Triple(materias, profesores, horarios)
        }.onEach { (materias, profesores, horarios) ->
            horariosExistentes = horarios
            _state.update {
                it.copy(
                    materias = materias,
                    profesores = profesores,
                    isLoading = false
                )
            }
        }.catch { e ->
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar datos: ${e.message}"
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun guardarHorario() {
        viewModelScope.launch {
            val materia = _state.value.materiaSeleccionada
            val profesor = _state.value.profesorSeleccionado

            if (materia == null || profesor == null) {
                _state.update { it.copy(errorMessage = "Debes seleccionar materia y profesor") }
                return@launch
            }

            if (_state.value.horaInicio >= _state.value.horaFin) {
                _state.update { it.copy(errorMessage = "La hora fin debe ser mayor a la hora inicio") }
                return@launch
            }

            if (hasOverlap(_state.value.diaSeleccionado, _state.value.horaInicio, _state.value.horaFin)) {
                _state.update { it.copy(errorMessage = "Ese horario se traslapa con otra clase existente") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val horario = Horario(
                usuarioId = userId,
                materiaId = materia.id,
                profesorId = profesor.id,
                dia = _state.value.diaSeleccionado,
                horaInicio = _state.value.horaInicio,
                horaFin = _state.value.horaFin,
                color = _state.value.colorSeleccionado
            )

            val result = addHorarioUseCase.invoke(horario)

            result.onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(isLoading = false, errorMessage = exception.message ?: "Error al guardar")
                }
            }
        }
    }

    private fun hasOverlap(dia: String, horaInicio: String, horaFin: String): Boolean {
        val newStart = toMinutes(horaInicio) ?: return false
        val newEnd = toMinutes(horaFin) ?: return false

        return horariosExistentes
            .asSequence()
            .filter { it.dia.equals(dia, ignoreCase = true) }
            .any { existing ->
                val existingStart = toMinutes(existing.horaInicio) ?: return@any false
                val existingEnd = toMinutes(existing.horaFin) ?: return@any false
                newStart < existingEnd && newEnd > existingStart
            }
    }

    private fun toMinutes(hourText: String): Int? {
        val parts = hourText.split(":")
        if (parts.size != 2) return null

        val h = parts[0].trim().toIntOrNull() ?: return null
        val m = parts[1].trim().toIntOrNull() ?: return null
        if (h !in 0..23 || m !in 0..59) return null

        return h * 60 + m
    }
}