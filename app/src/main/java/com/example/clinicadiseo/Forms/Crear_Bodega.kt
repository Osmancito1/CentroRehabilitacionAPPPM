package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Bodega
import com.example.clinicadiseo.Data_Models.BodegaRequest
import com.example.clinicadiseo.Data_Models.Producto
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearBodegaDialog(
    bodegaExistente: Bodega? = null,
    productosDisponibles: List<Producto>,
    onCrear: (BodegaRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var cantidadText by remember {
        mutableStateOf(bodegaExistente?.cantidad?.toString() ?: "")
    }
    var cantidad by remember {
        mutableStateOf(bodegaExistente?.cantidad ?: 0)
    }
    var ubicacion by remember { mutableStateOf(bodegaExistente?.ubicacion ?: "") }
    var idProducto by remember { mutableStateOf(bodegaExistente?.producto?.id_producto ?: 0) }

    var expandedProducto by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (bodegaExistente == null) "Nuevo Registro de Bodega" else "Editar Registro",
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

                // Selección de Producto
                ExposedDropdownMenuBox(
                    expanded = expandedProducto,
                    onExpandedChange = { expandedProducto = !expandedProducto },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val productoSeleccionado = productosDisponibles.find { it.id_producto == idProducto }
                    OutlinedTextField(
                        value = productoSeleccionado?.let { it.nombre } ?: "Seleccionar producto",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Producto", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProducto) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProducto,
                        onDismissRequest = { expandedProducto = false }
                    ) {
                        productosDisponibles.forEach {
                            DropdownMenuItem(
                                text = { Text(it.nombre, fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    idProducto = it.id_producto
                                    expandedProducto = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = cantidadText,
                    onValueChange = {
                        cantidadText = it
                        cantidad = it.toIntOrNull() ?: 0
                    },
                    label = { Text("Cantidad", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                // Campo para Ubicación
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
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
                    if (idProducto == 0 || ubicacion.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos obligatorios")
                        }
                        return@Button
                    }
                    val nuevoBodega = BodegaRequest(idProducto, cantidad, ubicacion)
                    onCrear(nuevoBodega)
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