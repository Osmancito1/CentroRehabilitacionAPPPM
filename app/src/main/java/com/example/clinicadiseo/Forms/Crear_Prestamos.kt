package com.example.clinicadiseo.Forms

import Prestamo
import PrestamoRequest
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPrestamoDialog(
    pacientes: List<Paciente>,
    productos: List<Producto>,
    prestamoExistente: Prestamo? = null,
    onCrear: (PrestamoRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Variables de estado
    var idPaciente by remember { mutableStateOf(prestamoExistente?.id_paciente ?: 0) }
    var idProducto by remember { mutableStateOf(prestamoExistente?.id_producto ?: 0) }
    var fechaPrestamo by remember { mutableStateOf(prestamoExistente?.fecha_prestamo ?: "") }
    var fechaDevolucion by remember { mutableStateOf(prestamoExistente?.fecha_devolucion ?: "") }
    var estado by remember { mutableStateOf(prestamoExistente?.estado ?: "Prestado") }

    var showPacienteMenu by remember { mutableStateOf(false) }
    var showProductoMenu by remember { mutableStateOf(false) }
    var showEstadoMenu by remember { mutableStateOf(false) }

    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val datePickerPrestamo = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            fechaPrestamo = formatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerDevolucion = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            fechaDevolucion = formatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val estados = listOf("Prestado" to "Prestado", "Devuelto" to "Devuelto")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (prestamoExistente == null) "Nuevo Préstamo" else "Editar Préstamo",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = showPacienteMenu,
                    onExpandedChange = { showPacienteMenu = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        value = pacientes.find { it.id_paciente == idPaciente }?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccionar paciente",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Paciente", fontFamily = poppinsbold) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPacienteMenu) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A5D1A),
                            focusedLabelColor = Color(0xFF1A5D1A),
                            cursorColor = Color(0xFF1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showPacienteMenu,
                        onDismissRequest = { showPacienteMenu = false }
                    ) {
                        pacientes.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.nombre} ${it.apellido}", fontFamily = poppins) },
                                onClick = {
                                    idPaciente = it.id_paciente
                                    showPacienteMenu = false
                                }
                            )
                        }
                    }
                }

                // Selector de Producto
                ExposedDropdownMenuBox(
                    expanded = showProductoMenu,
                    onExpandedChange = { showProductoMenu = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        value = productos.find { it.id_producto == idProducto }?.nombre ?: "Seleccionar producto",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Producto", fontFamily = poppinsbold) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProductoMenu) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A5D1A),
                            focusedLabelColor = Color(0xFF1A5D1A),
                            cursorColor = Color(0xFF1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showProductoMenu,
                        onDismissRequest = { showProductoMenu = false }
                    ) {
                        productos.forEach {
                            DropdownMenuItem(
                                text = { Text(it.nombre, fontFamily = poppins) },
                                onClick = {
                                    idProducto = it.id_producto
                                    showProductoMenu = false
                                }
                            )
                        }
                    }
                }

                // Selector de Fecha de préstamo
                OutlinedTextField(
                    value = fechaPrestamo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de préstamo", fontFamily = poppinsbold) },
                    trailingIcon = {
                        IconButton(onClick = { datePickerPrestamo.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF1A5D1A))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        focusedLabelColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                // Selector de Fecha de devolución
                OutlinedTextField(
                    value = fechaDevolucion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de devolución", fontFamily = poppins) },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDevolucion.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF1A5D1A))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        focusedLabelColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                // Selector de Estado
                ExposedDropdownMenuBox(
                    expanded = showEstadoMenu,
                    onExpandedChange = { showEstadoMenu = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        value = estados.find { it.first == estado }?.second ?: "Seleccionar estado",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado", fontFamily = poppinsbold) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEstadoMenu) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A5D1A),
                            focusedLabelColor = Color(0xFF1A5D1A),
                            cursorColor = Color(0xFF1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showEstadoMenu,
                        onDismissRequest = { showEstadoMenu = false }
                    ) {
                        estados.forEach {
                            DropdownMenuItem(
                                text = { Text(it.second, fontFamily = poppins) },
                                onClick = {
                                    estado = it.first
                                    showEstadoMenu = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (idPaciente == 0 || idProducto == 0 || fechaPrestamo.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos obligatorios")
                        }
                        return@Button
                    }
                    onCrear(
                        PrestamoRequest(
                            id_paciente = idPaciente,
                            id_producto = idProducto,
                            fecha_prestamo = fechaPrestamo,
                            fecha_devolucion = fechaDevolucion.ifBlank { null },
                            estado = estado
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
            ) {
                Text("Guardar", color = Color.White, fontFamily = poppins)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancelar", color = Color.White, fontFamily = poppins)
            }
        },
        containerColor = Color.White,
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = MaterialTheme.shapes.large
    )
}