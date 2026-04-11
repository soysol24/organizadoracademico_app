package com.example.organizadoracademico.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.profesor.GetProfesoresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    private val sessionManager: SessionManager,
    private val getHorariosUseCase: GetHorariosUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getProfesoresUseCase: GetProfesoresUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        cargarDatos()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CargarDatos -> cargarDatos()
            else -> {}
        }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userId = sessionManager.getUserId()
            val nombre = sessionManager.getUserName() ?: "Sol"
            val diaActual = obtenerDiaActual()

            try {
                val horarios = getHorariosUseCase(userId).first()
                val materias = getMateriasUseCase().first()
                val profesores = getProfesoresUseCase().first()

                val horariosDelDia = horarios.filter { it.dia == diaActual }

                val cards = horariosDelDia.mapNotNull { horario ->
                    val materia = materias.find { it.id == horario.materiaId }
                    val profesor = profesores.find { it.id == horario.profesorId }
                    if (materia != null && profesor != null) {
                        HorarioCardData(
                            nombre = materia.nombre,
                            profesor = profesor.nombre,
                            horaInicio = horario.horaInicio,
                            horaFin = horario.horaFin,
                            color = materia.color
                        )
                    } else null
                }

                _state.update {
                    it.copy(
                        usuarioNombre = nombre,
                        horariosHoy = cards,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun obtenerDiaActual(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> "Lunes"
        }
    }
}