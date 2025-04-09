package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.screens.poppins
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearDiagnosticoDialog(
    diagnosticoExistente: Diagnostico? = null,
    pacientesDisponibles: List<Paciente>,
    terapeutasDisponibles: List<Terapeuta>,
    onCrear: (DiagnosticoRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var descripcion by remember { mutableStateOf(diagnosticoExistente?.descripcion ?: "") }
    var tratamiento by remember { mutableStateOf(diagnosticoExistente?.tratamiento ?: "") }
    var idPaciente by remember { mutableStateOf(diagnosticoExistente?.id_paciente ?: 0) }
    var idTerapeuta by remember { mutableStateOf(diagnosticoExistente?.id_terapeuta ?: 0) }

    var expandedPaciente by remember { mutableStateOf(false) }
    var expandedTerapeuta by remember { mutableStateOf(false) }

    val pacienteSeleccionado = pacientesDisponibles.find { it.id_paciente == idPaciente }
    val terapeutaSeleccionado = terapeutasDisponibles.find { it.id_terapeuta == idTerapeuta }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (diagnosticoExistente == null) "Nuevo Diagnóstico" else "Editar Diagnóstico",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Dropdown Paciente
                ExposedDropdownMenuBox(
                    expanded = expandedPaciente,
                    onExpandedChange = { expandedPaciente = !expandedPaciente }
                ) {
                    OutlinedTextField(
                        value = pacienteSeleccionado?.let { "${it.nombre} ${it.apellido}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Paciente", fontFamily = poppins) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPaciente) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPaciente,
                        onDismissRequest = { expandedPaciente = false }
                    ) {
                        pacientesDisponibles.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.nombre} ${it.apellido}", fontFamily = poppins) },
                                onClick = {
                                    idPaciente = it.id_paciente
                                    expandedPaciente = false
                                }
                            )
                        }
                    }
                }

                // Dropdown Terapeuta
                ExposedDropdownMenuBox(
                    expanded = expandedTerapeuta,
                    onExpandedChange = { expandedTerapeuta = !expandedTerapeuta }
                ) {
                    OutlinedTextField(
                        value = terapeutaSeleccionado?.let { "${it.nombre} - ${it.especialidad}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Terapeuta", fontFamily = poppins) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTerapeuta) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTerapeuta,
                        onDismissRequest = { expandedTerapeuta = false }
                    ) {
                        terapeutasDisponibles.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.nombre} - ${it.especialidad}", fontFamily = poppins) },
                                onClick = {
                                    idTerapeuta = it.id_terapeuta
                                    expandedTerapeuta = false
                                }
                            )
                        }
                    }
                }

                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción", fontFamily = poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                )

                // Tratamiento
                OutlinedTextField(
                    value = tratamiento,
                    onValueChange = { tratamiento = it },
                    label = { Text("Tratamiento", fontFamily = poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (idPaciente == 0 || idTerapeuta == 0 || tratamiento.isBlank()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Completa todos los campos obligatorios")
                    }
                    return@Button
                }

                val nuevo = DiagnosticoRequest(idPaciente, idTerapeuta, descripcion, tratamiento)
                onCrear(nuevo)
                onDismiss()
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
            ) {
                Text("Guardar", color = Color.White, fontFamily = poppins)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Cancelar", color = Color.White, fontFamily = poppins)
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}




