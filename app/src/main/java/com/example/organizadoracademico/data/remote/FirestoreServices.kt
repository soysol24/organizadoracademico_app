package com.example.organizadoracademico.data.remote

import com.example.organizadoracademico.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfesorFirestoreService(private val db: FirebaseFirestore) {
    private val collection = db.collection("profesores")
    suspend fun save(profesor: Profesor) = collection.document(profesor.id.toString()).set(profesor).await()
    suspend fun delete(id: Int) = collection.document(id.toString()).delete().await()
}

class HorarioFirestoreService(private val db: FirebaseFirestore) {
    private val collection = db.collection("horarios")
    suspend fun save(horario: Horario) = collection.document(horario.id.toString()).set(horario).await()
    suspend fun delete(id: Int) = collection.document(id.toString()).delete().await()
}

class ImagenFirestoreService(private val db: FirebaseFirestore) {
    private val collection = db.collection("imagenes")
    suspend fun save(imagen: Imagen) = collection.document(imagen.id.toString()).set(imagen).await()
    suspend fun delete(id: Int) = collection.document(id.toString()).delete().await()
}

class UsuarioFirestoreService(private val db: FirebaseFirestore) {
    private val collection = db.collection("usuarios")
    suspend fun save(usuario: Usuario) = collection.document(usuario.id.toString()).set(usuario).await()
}