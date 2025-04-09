package com.example.clinicadiseo.Forms

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Encargado
import com.example.clinicadiseo.Data_Models.PacienteRequest
import com.example.clinicadiseo.Data_Models.Paciente
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPacienteDialog(
    pacienteExistente: Paciente? = null,
    encargadosDisponibles: List<Encargado>,
    onCrear: (PacienteRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(pacienteExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(pacienteExistente?.apellido ?: "") }
    var fechaNacimiento by remember { mutableStateOf(pacienteExistente?.fecha_nacimiento?.substring(0, 10) ?: "") }
    var telefono by remember { mutableStateOf(pacienteExistente?.telefono ?: "") }
    var direccion by remember { mutableStateOf(pacienteExistente?.direccion ?: "") }
    var idEncargado by remember { mutableStateOf(pacienteExistente?.encargado?.id_encargado ?: 0) }
    var expandedEncargado by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.set(year, month, dayOfMonth)
            fechaNacimiento = sdf.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (pacienteExistente == null) "Nuevo Paciente" else "Editar Paciente",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    "Nombre" to nombre,
                    "Apellido" to apellido,
                    "Teléfono" to telefono,
                    "Dirección" to direccion
                ).forEachIndexed { index, pair ->
                    val (label, value) = pair
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            when (index) {
                                0 -> nombre = it
                                1 -> apellido = it
                                2 -> telefono = it
                                3 -> direccion = it
                            }
                        },
                        label = {
                            Text(label, fontFamily = poppins, style = MaterialTheme.typography.bodyMedium)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A5D1A),
                            focusedLabelColor = Color(0xFF1A5D1A),
                            cursorColor = Color(0xFF1A5D1A)
                        )
                    )
                }

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = {},
                    label = {
                        Text("Fecha de nacimiento", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = Color(0xFF1A5D1A))
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        focusedLabelColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expandedEncargado,
                    onExpandedChange = { expandedEncargado = !expandedEncargado },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val encargadoSeleccionado = encargadosDisponibles.find { it.id_encargado == idEncargado }
                    OutlinedTextField(
                        value = encargadoSeleccionado?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccionar encargado",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Encargado", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEncargado) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A5D1A),
                            focusedLabelColor = Color(0xFF1A5D1A),
                            cursorColor = Color(0xFF1A5D1A)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedEncargado,
                        onDismissRequest = { expandedEncargado = false }
                    ) {
                        encargadosDisponibles.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.nombre} ${it.apellido}", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    idEncargado = it.id_encargado
                                    expandedEncargado = false
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
                    if (nombre.isBlank() || apellido.isBlank() || fechaNacimiento.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos requeridos")
                        }
                        return@Button
                    }

                    val nuevoPaciente = PacienteRequest(
                        nombre = nombre,
                        apellido = apellido,
                        fecha_nacimiento = fechaNacimiento,
                        telefono = telefono,
                        direccion = direccion,
                        id_encargado = idEncargado
                    )

                    onCrear(nuevoPaciente)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Guardar", fontFamily = poppins, color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Cancelar", fontFamily = poppins, color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}
