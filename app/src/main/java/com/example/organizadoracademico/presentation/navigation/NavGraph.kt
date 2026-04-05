package com.example.organizadoracademico.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.organizadoracademico.presentation.horario.crear.CrearHorarioScreen
import com.example.organizadoracademico.presentation.horario.ver.VerHorarioScreen
import com.example.organizadoracademico.presentation.imagen.camara.CamaraScreen
import com.example.organizadoracademico.presentation.imagen.galeria.GaleriaScreen
import com.example.organizadoracademico.presentation.imagen.nota.NotaScreen
import com.example.organizadoracademico.presentation.login.LoginScreen
import com.example.organizadoracademico.presentation.main.MainScreen
import com.example.organizadoracademico.presentation.materia.MisMateriasScreen
import com.example.organizadoracademico.presentation.perfil.PerfilScreen
import com.example.organizadoracademico.presentation.registro.RegistroScreen
import com.example.organizadoracademico.presentation.imagen.detalle.DetalleImagenScreen

@Composable
fun NavGraph(
    isLoggedIn: Boolean = false
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // Decidimos el punto de entrada: Login o Main
        startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route,
        modifier = Modifier
    ) {
        // --- FLUJO DE AUTENTICACIÓN ---
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        // Al entrar al Main, borramos el Login del historial
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(route = Screen.Registro.route) {
            RegistroScreen(navController = navController)
        }

        // --- PANTALLAS PRINCIPALES ---
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController)
        }

        composable(route = Screen.MisMaterias.route) {
            MisMateriasScreen(navController = navController)
        }

        composable(route = Screen.Perfil.route) {
            // Quitamos el parámetro onLogout para mantener la sesión siempre activa
            PerfilScreen(navController = navController)
        }

        // --- GESTIÓN DE HORARIOS ---
        composable(route = Screen.CrearHorario.route) {
            CrearHorarioScreen(navController = navController)
        }

        composable(route = Screen.VerHorario.route) {
            VerHorarioScreen(navController = navController)
        }

        // --- GALERÍA Y CÁMARA (Con paso de parámetros) ---
        composable(
            route = Screen.Galeria.route,
            arguments = listOf(navArgument("materiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getInt("materiaId") ?: 0
            GaleriaScreen(
                navController = navController,
                materiaId = materiaId
            )
        }

        composable(
            route = Screen.Camara.route,
            arguments = listOf(navArgument("materiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getInt("materiaId") ?: 0
            CamaraScreen(
                navController = navController,
                materiaId = materiaId
            )
        }

        // --- NOTAS Y DETALLES ---
        composable(
            route = Screen.Nota.route,
            arguments = listOf(
                navArgument("materiaId") { type = NavType.IntType },
                navArgument("uri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val materiaId = backStackEntry.arguments?.getInt("materiaId") ?: 0
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            NotaScreen(
                navController = navController,
                materiaId = materiaId,
                imageUri = uri
            )
        }

        composable(
            route = Screen.DetalleImagen.route,
            arguments = listOf(navArgument("imagenId") { type = NavType.IntType })
        ) { backStackEntry ->
            val imagenId = backStackEntry.arguments?.getInt("imagenId") ?: 0
            DetalleImagenScreen(
                navController = navController,
                imagenId = imagenId
            )
        }
    }
}