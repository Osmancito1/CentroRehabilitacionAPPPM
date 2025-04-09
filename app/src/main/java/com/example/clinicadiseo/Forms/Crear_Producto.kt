package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.Data_Models.Producto
import com.example.clinicadiseo.Data_Models.ProductoRequest
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProductoDialog(
    productoExistente: Producto? = null,
    onCrear: (ProductoRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(productoExistente?.categoria ?: "") }
    var cantidadDisponibleText by remember {
        mutableStateOf(productoExistente?.cantidad_disponible?.toString() ?: "")
    }

    val cantidadError = cantidadDisponibleText.isNotBlank() &&
            (cantidadDisponibleText.toIntOrNull() == null || cantidadDisponibleText.toIntOrNull()!! < 0)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (productoExistente == null) "Nuevo Producto" else "Editar Producto",
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
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                Column {
                    OutlinedTextField(
                        value = cantidadDisponibleText,
                        onValueChange = {
                            cantidadDisponibleText = it
                        },
                        label = { Text("Cantidad Disponible", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        singleLine = true,
                        isError = cantidadError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A),
                            errorBorderColor = Color.Red,
                            errorLabelColor = Color.Red,
                            errorCursorColor = Color.Red
                        )
                    )

                    if (cantidadError) {
                        Text(
                            text = "Ingresa un número válido y positivo",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontFamily = poppins,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (
                        nombre.isBlank() ||
                        descripcion.isBlank() ||
                        categoria.isBlank() ||
                        cantidadDisponibleText.isBlank() ||
                        cantidadDisponibleText.toIntOrNull() == null ||
                        cantidadDisponibleText.toInt() < 0
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos correctamente")
                        }
                        return@Button
                    }

                    val cantidad = cantidadDisponibleText.toInt()
                    val nuevo = ProductoRequest(nombre, descripcion, categoria, cantidad)
                    onCrear(nuevo)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    "Guardar",
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
