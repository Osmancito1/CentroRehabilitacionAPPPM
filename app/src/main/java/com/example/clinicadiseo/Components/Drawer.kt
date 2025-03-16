package com.example.clinicadiseo.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonalInjury
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import poppins
import poppinsbold


@Composable
fun DrawerContent(
    appNavController: NavHostController,
    sectionNavController: NavHostController,
    onClose: () -> Unit
) {
    val items = listOf(
        Triple("Inicio", Icons.Default.Home, "categorias"),
        Triple("Pacientes", Icons.Default.PersonalInjury, Screen.Pacientes.route),
        Triple("Encargados", Icons.Default.People, Screen.Encargados.route),
        Triple("Terapeutas", Icons.Default.Healing, Screen.Terapeutas.route),
        Triple("Diagnósticos", Icons.Default.MedicalInformation, Screen.Diagnosticos.route),
        Triple("Citas", Icons.Default.Medication, Screen.Citas.route),
        Triple("Productos", Icons.Default.ProductionQuantityLimits, Screen.Productos.route),
        Triple("Compras", Icons.Default.AddShoppingCart, Screen.Compras.route),
        Triple("Préstamos", Icons.Default.Sell, Screen.Prestamos.route),
        Triple("Bodegas", Icons.Default.Inventory2, Screen.Bodegas.route),
        Triple("Usuarios", Icons.Default.SupervisedUserCircle, Screen.Usuarios.route)
    )

    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        items.forEach { (title, icon, route) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .clickable {
                        sectionNavController.navigate(route)
                        onClose()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(27.dp))
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = title, color = Color.White, fontFamily = poppins, fontSize = 19.sp)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                appNavController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp)
                .width(230.dp)
                .height(60.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
            Spacer(modifier = Modifier.width(14.dp))
            Text("Cerrar Sesión", color = Color.White, fontFamily = poppinsbold, fontSize = 16.sp)
        }
    }
}