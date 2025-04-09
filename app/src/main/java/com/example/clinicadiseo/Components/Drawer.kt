package com.example.clinicadiseo.Components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clinicadiseo.screens.Screen
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.utils.decodeJwt
import kotlinx.coroutines.delay

@Composable
fun DrawerContent(
    appNavController: NavHostController,
    sectionNavController: NavHostController,
    rolUsuario: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        DrawerHeader()
        DrawerMenuItems(sectionNavController, rolUsuario, onClose)
        Spacer(modifier = Modifier.weight(1f))
        LogoutButton(appNavController)
    }
}

@Composable
fun DrawerHeader() {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("Usuario") }
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (!token.isNullOrEmpty()) {
            val payload = decodeJwt(token)
            userName = payload["nombre"] ?: "Usuario"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonalInjury,
            contentDescription = "Ícono de perfil",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("Bienvenido", color = Color.White, fontSize = 18.sp, fontFamily = poppinsbold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Usuario: ",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = poppins
            )
            Text(
                text = userName,
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = poppins,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DrawerMenuItems(sectionNavController: NavHostController, rolUsuario: String, onClose: () -> Unit) {
    val items = if (rolUsuario == "Administrador") {
        listOf(
            Triple("Inicio", Icons.Default.Home, "categorias"),
            Triple("Pacientes", Icons.Default.PersonalInjury, Screen.Pacientes.route),
            Triple("Encargados", Icons.Default.People, Screen.Encargados.route),
            Triple("Terapeutas", Icons.Default.Healing, Screen.Terapeutas.route),
            Triple("Diagnósticos", Icons.Default.MedicalInformation, Screen.Diagnosticos.route),
            Triple("Citas", Icons.Default.Medication, Screen.Citas.route),
            Triple("Productos", Icons.Default.ProductionQuantityLimits, Screen.Productos.route),
            Triple("Compras", Icons.Default.AddShoppingCart, Screen.Compras.route),
            Triple("Bodegas", Icons.Default.Inventory2, Screen.Bodegas.route),
            Triple("Usuarios", Icons.Default.SupervisedUserCircle, Screen.Usuarios.route)
        )
    } else {
        listOf(
            Triple("Pacientes", Icons.Default.PersonalInjury, Screen.Pacientes.route),
            Triple("Encargados", Icons.Default.People, Screen.Encargados.route),
            Triple("Terapeutas", Icons.Default.Healing, Screen.Terapeutas.route),
            Triple("Diagnósticos", Icons.Default.MedicalInformation, Screen.Diagnosticos.route),
            Triple("Citas", Icons.Default.Medication, Screen.Citas.route)
        )
    }

    Column {
        items.forEachIndexed { index, (title, icon, route) ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(index * 100L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(initialOffsetX = { -50 })
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            sectionNavController.navigate(route)
                            onClose()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(27.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(text = title, color = Color.White, fontSize = 19.sp, fontFamily = poppins)
                }
            }
        }
    }
}


@Composable
fun LogoutButton(appNavController: NavHostController) {
    var pressed by remember { mutableStateOf(false) }

    Button(
        onClick = {
            pressed = true
            appNavController.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = Modifier
            .width(230.dp)
            .height(50.dp)
            .graphicsLayer(scaleX = if (pressed) 0.95f else 1f, scaleY = if (pressed) 0.95f else 1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    }
                )
            }
    ) {
        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
        Spacer(modifier = Modifier.width(10.dp))
        Text("Cerrar Sesión", color = Color.White, fontFamily = poppinsbold, fontSize = 16.sp)
    }
}




