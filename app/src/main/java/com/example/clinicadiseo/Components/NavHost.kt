package com.example.clinicadiseo.Components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clinicadiseo.pantallas.*
import com.example.clinicadiseo.pantallasprincipales.LoginScreen
import com.example.clinicadiseo.screens.HomeScreen
import com.example.clinicadiseo.screens.Screen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }

        // Secciones internas
        composable(Screen.Pacientes.route) { PacientesScreen() }
        composable(Screen.Encargados.route) { EncargadosScreen() }
        composable(Screen.Terapeutas.route) { TerapeutasScreen() }
        composable(Screen.Diagnosticos.route) { DiagnosticosScreen() }
        composable(Screen.Citas.route) { CitasScreen() }
        composable(Screen.Productos.route) { ProductosScreen() }
        composable(Screen.Compras.route) { ComprasScreen(navController) }
        composable(Screen.Prestamos.route) { PrestamosScreen() }
        composable(Screen.Bodegas.route) { BodegasScreen() }
        composable(Screen.Usuarios.route) { UsuariosScreen() }
        composable("compras_form/{id}") { backStackEntry ->
            ComprasFormScreen(navController = navController, backStackEntry = backStackEntry)
        }

    }
}