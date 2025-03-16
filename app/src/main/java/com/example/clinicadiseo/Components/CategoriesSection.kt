package com.example.clinicadiseo.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonalInjury
import androidx.compose.material.icons.outlined.ProductionQuantityLimits
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.SupervisedUserCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import poppinsbold



@Composable
fun CategoriesSection(navController: NavHostController) {
    val categories = listOf(
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

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(36.dp))
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
                .height(600.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category, navController)
            }
        }
    }
}
