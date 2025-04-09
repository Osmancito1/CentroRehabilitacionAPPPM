package com.example.clinicadiseo.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clinicadiseo.screens.Screen
import com.example.clinicadiseo.screens.poppinsbold

@Composable
fun CategoriesSection(navController: NavHostController, screenHeight: Dp, rolUsuario: String) {

    val categories = if (rolUsuario == "Administrador") {
        listOf(
            Category("Pacientes", Icons.Outlined.PersonalInjury, Screen.Pacientes.route),
            Category("Encargados", Icons.Outlined.People, Screen.Encargados.route),
            Category("Terapeutas", Icons.Outlined.Healing, Screen.Terapeutas.route),
            Category("Diagnósticos", Icons.Outlined.MedicalInformation, Screen.Diagnosticos.route),
            Category("Citas", Icons.Outlined.Medication, Screen.Citas.route),
            Category("Productos", Icons.Outlined.ProductionQuantityLimits, Screen.Productos.route),
            Category("Compras", Icons.Outlined.AddShoppingCart, Screen.Compras.route),
            Category("Préstamos", Icons.Outlined.Sell, Screen.Prestamos.route),
            Category("Bodegas", Icons.Outlined.Inventory2, Screen.Bodegas.route),
            Category("Usuarios", Icons.Outlined.SupervisedUserCircle, Screen.Usuarios.route)
        )
    } else {
        listOf(
            Category("Pacientes", Icons.Outlined.PersonalInjury, Screen.Pacientes.route),
            Category("Encargados", Icons.Outlined.People, Screen.Encargados.route),
            Category("Terapeutas", Icons.Outlined.Healing, Screen.Terapeutas.route),
            Category("Diagnósticos", Icons.Outlined.MedicalInformation, Screen.Diagnosticos.route),
            Category("Citas", Icons.Outlined.Medication, Screen.Citas.route)
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Página Principal",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = poppinsbold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.8f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    CategoryCard(category, navController, screenHeight)
                }
            }
        }
    }
}
