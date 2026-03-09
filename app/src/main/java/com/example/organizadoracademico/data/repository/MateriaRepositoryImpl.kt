package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.local.util.DataInitializer
import com.example.organizadoracademico.data.remote.MateriaFirestoreService
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MateriaRepositoryImpl(
    private val dao: MateriaDao,
    private val remoteService: MateriaFirestoreService,
    private val db: AppDatabase
) : IMateriaRepository {

    // CAMBIO: Ahora la función no recibe parámetros porque el catálogo es global
    override fun getAllMaterias(): Flow<List<Materia>> {
        // Disparo de carga inicial global si la tabla está vacía
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Usamos el método global getCountGlobal que pusimos en el DAO
                if (dao.getCountGlobal() == 0) {
                    // Usamos el método que no pide userId
                    DataInitializer(db).populateIfEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Retornamos todas las materias (sin filtrar por usuario)
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMateria(materia: Materia) {
        // La entidad ya no tiene userId
        dao.insert(materia.toEntity())

        try {
            remoteService.saveMateria(materia)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteMateria(id: Int) {
        dao.deleteById(id)

        try {
            remoteService.deleteMateria(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}