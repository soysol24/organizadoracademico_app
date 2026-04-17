package com.example.organizadoracademico.presentation.imagen.galeria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager // <-- Importamos
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.usercase.imagen.DeleteImagenUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GaleriaViewModel(
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val deleteImagenUseCase: DeleteImagenUseCase,
    private val sessionManager: SessionManager // <-- 1. Inyectamos
) : ViewModel() {

    private val _state = MutableStateFlow(GaleriaState())
    val state: StateFlow<GaleriaState> = _state.asStateFlow()

    // 2. Obtenemos el ID del usuario actual
    private val userId = sessionManager.getUserId()

    fun onEvent(event: GaleriaEvent) {
        when (event) {
            is GaleriaEvent.CargarImagenes -> cargarImagenes(event.materiaId)
            is GaleriaEvent.EliminarImagen -> eliminarImagen(event.imagenId)
            is GaleriaEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun refresh(materiaId: Int) {
        cargarImagenes(materiaId)
    }

    private fun cargarImagenes(materiaId: Int) {
        _state.update { it.copy(isLoading = true) }

        val materiaInfoFlow = getMateriasUseCase().map { materias ->
            materias.find { it.id == materiaId }
        }

        // 3. Pasamos el userId al caso de uso para filtrar la galería
        val imagenesFlow = getImagenesPorMateriaUseCase(materiaId, userId)

        combine(materiaInfoFlow, imagenesFlow) { materia, imagenes ->
            Pair(materia, imagenes)
        }.onEach { (materia, imagenes) ->
            val agrupadas = agruparImagenesPorFecha(imagenes)
            _state.update {
                it.copy(
                    isLoading = false,
                    materia = materia,
                    imagenes = imagenes,
                    imagenesAgrupadas = agrupadas
                )
            }
        }.catch { e ->
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar imágenes: ${e.message}"
                )
            }
        }.launchIn(viewModelScope)
    }

    // ... (eliminarImagen y agruparImagenesPorFecha se mantienen igual)
    private fun eliminarImagen(imagenId: Int) {
        viewModelScope.launch {
            try {
                deleteImagenUseCase(imagenId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(errorMessage = e.message ?: "Error al eliminar imagen")
                }
            }
        }
    }

    private fun agruparImagenesPorFecha(imagenes: List<Imagen>): Map<String, List<Imagen>> {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es"))
        return imagenes.groupBy { imagen ->
            dateFormat.format(Date(imagen.fecha))
        }.toSortedMap(reverseOrder())
    }
}