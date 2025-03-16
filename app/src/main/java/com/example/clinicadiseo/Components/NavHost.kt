package com.example.clinicadiseo.Components
import HomeScreen
import LoginScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clinicadiseo.pantallas.BodegasScreen
import com.example.clinicadiseo.pantallas.CitasScreen
import com.example.clinicadiseo.pantallas.ComprasScreen
import com.example.clinicadiseo.pantallas.DiagnosticoScreen
import com.example.clinicadiseo.pantallas.EncargadosScreen
import com.example.clinicadiseo.pantallas.PacientesScreen
import com.example.clinicadiseo.pantallas.ProductosScreen
import com.example.clinicadiseo.pantallas.TerapeutasScreen
import com.example.clinicadiseo.pantallas.UsuariosScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }

        // Secciones internas
        composable(Screen.Pacientes.route) { PacientesScreen() }
        composable(Screen.Encargados.route) { EncargadosScreen() }
        composable(Screen.Terapeutas.route) { TerapeutasScreen() }
        composable(Screen.Diagnosticos.route) { DiagnosticoScreen() }
        composable(Screen.Citas.route) { CitasScreen() }
        composable(Screen.Productos.route) { ProductosScreen() }
        composable(Screen.Compras.route) { ComprasScreen() }
        composable(Screen.Prestamos.route) { Text("Pantalla de Préstamos") } // temporal
        composable(Screen.Bodegas.route) { BodegasScreen() }
        composable(Screen.Usuarios.route) { UsuariosScreen() }
    }
}
