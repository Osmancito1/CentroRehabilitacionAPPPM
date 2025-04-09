package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Bodega
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SacarProductoDialog(
    bodegaSeleccionada: Bodega?,
    onSacarProducto: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var cantidadText by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf(0) }

    if (bodegaSeleccionada == null) {
        onDismiss()
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sacar Producto",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Producto: ${bodegaSeleccionada.producto?.nombre ?: "Sin nombre"}",
                    fontFamily = poppinsbold,
                    color = Color.Black
                )
                Text(
                    text = "Cantidad disponible: ${bodegaSeleccionada.cantidad}",
                    fontFamily = poppins,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = cantidadText,
                    onValueChange = {
                        cantidadText = it
                        cantidad = it.toIntOrNull() ?: 0
                    },
                    label = { Text("Cantidad a quitar", fontFamily = poppins) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cantidad <= 0 || cantidad > bodegaSeleccionada.cantidad) {
                        scope.launch {
                            println("Error: Cantidad inválida")
                        }
                        return@Button
                    }
                    onSacarProducto(cantidad)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    "Sacar",
                    fontFamily = poppins,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    "Cancelar",
                    fontFamily = poppins,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}