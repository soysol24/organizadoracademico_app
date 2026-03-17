package com.example.organizadoracademico.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import com.example.organizadoracademico.presentation.theme.FondoPrincipal
import com.example.organizadoracademico.presentation.theme.MoradoNeon
import com.example.organizadoracademico.presentation.theme.TextoBlanco
import com.example.organizadoracademico.presentation.theme.TextoGris
import com.example.organizadoracademico.presentation.animation.pulseEffect
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar cuando el login es exitoso
    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Logo / Icono
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MoradoNeon.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡",
                    fontSize = 48.sp,
                    color = MoradoNeon
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título
            Text(
                text = "¡HOLA DE NUEVO",
                fontSize = 24.sp,
                color = TextoGris,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "TE ECHAMOS DE MENOS",
                fontSize = 14.sp,
                color = MoradoNeon,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Email
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                placeholder = { Text("correo@ejemplo.com", color = TextoGris) },
                leadingIcon = { Text("✉️", color = MoradoNeon) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MoradoNeon,
                    unfocusedBorderColor = TextoGris,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Password
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                placeholder = { Text("••••••••", color = TextoGris) },
                leadingIcon = { Text("🔒", color = MoradoNeon) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MoradoNeon,
                    unfocusedBorderColor = TextoGris,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de error
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botón de login
            Button(
                onClick = { viewModel.onEvent(LoginEvent.LoginClicked) },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoNeon
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .pulseEffect(!state.isLoading)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = TextoBlanco,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 16.sp,
                        color = TextoBlanco
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Línea decorativa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TextoGris.copy(alpha = 0.3f))
            )

            // BOTÓN DE REGISTRO (nuevo)
            TextButton(
                onClick = {
                    navController.navigate(Screen.Registro.route)  // <-- Navega a registro
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = MoradoNeon
                )
            }
        }
    }
}