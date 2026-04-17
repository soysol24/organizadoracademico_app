package com.example.organizadoracademico.presentation.horario.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager // <-- Importante
import com.example.organizadoracademico.domain.exception.HorarioDuplicadoException
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.usercase.horario.AddHorarioUseCase
import com.example.organizadoracademico.domain.usercase.horario.ValidateHorarioTraslapeUseCase
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
    private val addHorarioUseCase: AddHorarioUseCase,
    private val validateHorarioTraslapeUseCase: ValidateHorarioTraslapeUseCase,
    private val sessionManager: SessionManager // <-- 1. Inyectamos el SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(CrearHorarioState())
    val state: StateFlow<CrearHorarioState> = _state.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    // ... (onEvent y cargarDatosIniciales se mantienen igual)
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

        combine(materiasFlow, profesoresFlow) { materias, profesores ->
            Pair(materias, profesores)
        }.onEach { (materias, profesores) ->
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
            val userId = sessionManager.getUserId()
            val materia = _state.value.materiaSeleccionada
            val profesor = _state.value.profesorSeleccionado

            if (userId <= 0) {
                _state.update { it.copy(errorMessage = "Sesion invalida. Vuelve a iniciar sesion") }
                return@launch
            }

            if (materia == null || profesor == null) {
                _state.update { it.copy(errorMessage = "Debes seleccionar materia y profesor") }
                return@launch
            }

            val horaInicioMin = parseHourToMinutes(_state.value.horaInicio)
            val horaFinMin = parseHourToMinutes(_state.value.horaFin)
            if (horaInicioMin == null || horaFinMin == null) {
                _state.update { it.copy(errorMessage = "Formato de hora invalido") }
                return@launch
            }

            if (horaInicioMin >= horaFinMin) {
                _state.update { it.copy(errorMessage = "La hora fin debe ser mayor a la hora inicio") }
                return@launch
            }

            val horaInicioNormalizada = normalizeHour(_state.value.horaInicio)
            val horaFinNormalizada = normalizeHour(_state.value.horaFin)
            val existeTraslape = validateHorarioTraslapeUseCase(
                usuarioId = userId,
                dia = _state.value.diaSeleccionado,
                horaInicio = horaInicioNormalizada,
                horaFin = horaFinNormalizada
            )
            if (existeTraslape) {
                _state.update {
                    it.copy(errorMessage = "Ya existe un horario traslapado en esa franja. Elige otra hora")
                }
                return@launch
            }

            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // 3. AGREGAMOS EL usuarioId AL CONSTRUCTOR DEL HORARIO
            val horario = Horario(
                usuarioId = userId, // <-- Aquí se soluciona el error
                materiaId = materia.id,
                profesorId = profesor.id,
                dia = _state.value.diaSeleccionado,
                horaInicio = horaInicioNormalizada,
                horaFin = horaFinNormalizada,
                color = _state.value.colorSeleccionado
            )

            val result = addHorarioUseCase.invoke(horario)

            result.onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                val errorMessage = if (exception is HorarioDuplicadoException) {
                    "No se puede colocar un horario en la misma hora que otro"
                } else {
                    exception.message ?: "Error al guardar"
                }
                _state.update {
                    it.copy(isLoading = false, errorMessage = errorMessage)
                }
            }
        }
    }

    private fun parseHourToMinutes(hora: String): Int? {
        val parts = hora.split(":")
        if (parts.size != 2) return null
        val h = parts[0].trim().toIntOrNull() ?: return null
        val m = parts[1].trim().toIntOrNull() ?: return null
        if (h !in 0..23 || m !in 0..59) return null
        return h * 60 + m
    }

    private fun normalizeHour(hora: String): String {
        val minutes = parseHourToMinutes(hora) ?: return hora
        val h = minutes / 60
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }
}