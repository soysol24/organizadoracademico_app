package com.example.organizadoracademico.presentation.registro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.theme.*
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: RegistroViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar al login después de registro exitoso
    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Título
        Text(
            text = "CREAR CUENTA",
            fontSize = 28.sp,
            color = MoradoNeon,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Campo Nombre
        OutlinedTextField(
            value = state.nombre,
            onValueChange = { viewModel.onEvent(RegistroEvent.NombreCambio(it)) },
            label = { Text("Nombre completo", color = TextoGris) },
            leadingIcon = { Text("👤", color = MoradoNeon) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(RegistroEvent.EmailCambio(it)) },
            label = { Text("Email", color = TextoGris) },
            leadingIcon = { Text("✉️", color = MoradoNeon) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(RegistroEvent.PasswordCambio(it)) },
            label = { Text("Contraseña", color = TextoGris) },
            leadingIcon = { Text("🔒", color = MoradoNeon) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Confirmar Contraseña
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onEvent(RegistroEvent.ConfirmPasswordCambio(it)) },
            label = { Text("Confirmar contraseña", color = TextoGris) },
            leadingIcon = { Text("🔒", color = MoradoNeon) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieCards,
                unfocusedContainerColor = SuperficieCards,
                focusedBorderColor = MoradoNeon,
                unfocusedBorderColor = BordeNeon,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Registrar
        Button(
            onClick = { viewModel.onEvent(RegistroEvent.Registrar) },
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeMatrix
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pulseEffect(!state.isLoading)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = TextoBlanco, modifier = Modifier.size(24.dp))
            } else {
                Text("REGISTRARSE", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link a Login
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                color = MoradoNeon
            )
        }
    }
}
