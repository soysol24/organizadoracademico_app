package com.example.organizadoracademico.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")

    object Registro : Screen("registro")  // <-- NUEVA RUTA

    object Main : Screen("main")
    object CrearHorario : Screen("crear_horario")
    object VerHorario : Screen("ver_horario")
    object MisMaterias : Screen("mis_materias")
    object Galeria : Screen("galeria/{materiaId}") {
        fun passMateriaId(materiaId: Int): String = "galeria/$materiaId"
    }
    object Camara : Screen("camara/{materiaId}") {
        fun passMateriaId(materiaId: Int): String = "camara/$materiaId"
    }
    object Nota : Screen("nota/{materiaId}/{uri}") {
        fun passParams(materiaId: Int, uri: String): String = "nota/$materiaId/$uri"
    }
    object DetalleImagen : Screen("detalle_imagen/{imagenId}") {
        fun passImagenId(imagenId: Int): String = "detalle_imagen/$imagenId"
    }
    object Perfil : Screen("perfil")
}