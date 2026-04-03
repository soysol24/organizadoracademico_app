package com.example.organizadoracademico.data.sync

object SyncEntityType {
    const val IMAGEN = "IMAGEN"
    const val HORARIO = "HORARIO"
}

object SyncAction {
    const val CREATE = "CREATE"
    const val DELETE = "DELETE"
}

data class ImagenDeletePayload(val remoteId: Int)
data class HorarioDeletePayload(val remoteId: Int)

