package com.example.clinicadiseo.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.screens.poppinsbold

@Composable
fun withAdminAccess(rolUsuario: String, content: @Composable () -> Unit) {
    if (rolUsuario == "Administrador") {
        content()
    } else {
        AccesoDenegado()
    }
}

@Composable
fun AccesoDenegado() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Acceso denegado",
            color = Color.Red,
            fontSize = 20.sp,
            fontFamily = poppinsbold
        )
    }
}
