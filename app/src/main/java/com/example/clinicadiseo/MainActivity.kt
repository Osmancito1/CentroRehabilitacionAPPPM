package com.example.clinicadiseo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.clinicadiseo.Components.AppNavigation
import com.example.clinicadiseo.Data_Models.Paciente
import com.example.clinicadiseo.pantallas.BodegasScreen
import com.example.clinicadiseo.pantallas.CitasScreen
import com.example.clinicadiseo.pantallas.DiagnosticosScreen
import com.example.clinicadiseo.pantallas.EncargadosScreen
import com.example.clinicadiseo.pantallas.PacientesScreen
import com.example.clinicadiseo.pantallas.PrestamosScreen
import com.example.clinicadiseo.pantallas.ProductosScreen
import com.example.clinicadiseo.pantallas.UsuariosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        Handler(Looper.getMainLooper()).postDelayed({
            splashScreen.setKeepOnScreenCondition { false }
        }, 3000)

        setContent {
          val navController = rememberNavController()
            AppNavigation(navController = navController)


        }
    }
}





