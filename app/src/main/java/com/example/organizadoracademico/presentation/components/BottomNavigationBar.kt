package com.example.organizadoracademico.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.organizadoracademico.presentation.navigation.Screen
import androidx.compose.foundation.background


@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? = null,
    modifier: Modifier = Modifier  // ← AHORA SÍ ACEPTA MODIFIER
) {
    Surface(
        modifier = modifier,  // ← SE USA AQUÍ
        color = Color(0xFF1A1A2E),
        tonalElevation = 8.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = currentRoute == Screen.Main.route,
                onClick = { navController.navigate(Screen.Main.route) }
            )
            BottomNavItem(
                icon = Icons.Default.CalendarToday,
                label = "Horario",
                isSelected = currentRoute == Screen.VerHorario.route,
                onClick = { navController.navigate(Screen.VerHorario.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Add,
                label = "",
                isCentral = true,
                onClick = { navController.navigate(Screen.CrearHorario.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Folder,
                label = "Materias",
                isSelected = currentRoute == Screen.MisMaterias.route,
                onClick = { navController.navigate(Screen.MisMaterias.route) }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                isSelected = currentRoute == Screen.Perfil.route,
                onClick = { navController.navigate(Screen.Perfil.route) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isCentral: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = if (isCentral) Modifier.size(56.dp) else Modifier
    ) {
        Box(
            modifier = if (isCentral) Modifier
                .background(
                    color = Color(0xFF6681EA),
                    shape = CircleShape
                )
                .padding(12.dp)
            else Modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = when {
                    isCentral -> Color.White
                    isSelected -> Color(0xFF6681EA)
                    else -> Color.White.copy(alpha = 0.5f)
                },
                modifier = Modifier.size(if (isCentral) 28.dp else 24.dp)
            )
            if (!isCentral && label.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = if (isSelected) Color(0xFF6681EA) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}