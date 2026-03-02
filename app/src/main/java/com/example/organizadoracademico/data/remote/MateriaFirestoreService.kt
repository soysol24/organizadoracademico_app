package com.example.organizadoracademico.data.remote

import com.example.organizadoracademico.domain.model.Materia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MateriaFirestoreService(private val db: FirebaseFirestore) {

    private val collection = db.collection("materias")

    suspend fun saveMateria(materia: Materia) {
        // Usamos el ID de la materia como nombre del documento
        collection.document(materia.id.toString())
            .set(materia)
            .await()
    }

    suspend fun deleteMateria(id: Int) {
        collection.document(id.toString())
            .delete()
            .await()
    }

    suspend fun getAllMaterias(): List<Materia> {
        return try {
            collection.get().await().toObjects(Materia::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}