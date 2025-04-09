package com.example.clinicadiseo.Forms

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Cita
import com.example.clinicadiseo.Data_Models.CitaRequest
import com.example.clinicadiseo.Data_Models.Paciente
import com.example.clinicadiseo.Data_Models.Terapeuta
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CrearCitaDialog(
    citaExistente: Cita? = null,
    pacientesDisponibles: List<Paciente>,
    terapeutasDisponibles: List<Terapeuta>,
    citasExistentes: List<Cita>,
    onCrear: (CitaRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var idPaciente by remember { mutableStateOf(citaExistente?.id_paciente ?: 0) }
    var idTerapeuta by remember { mutableStateOf(citaExistente?.id_terapeuta ?: 0) }
    var tipoTerapia by remember { mutableStateOf(citaExistente?.tipo_terapia ?: "Fisica") }
    var fecha by remember { mutableStateOf(citaExistente?.fecha ?: "") }
    var horaInicio by remember { mutableStateOf(citaExistente?.hora_inicio ?: "") }
    var horaFin by remember { mutableStateOf(citaExistente?.hora_fin ?: "") }
    var duracionMin by remember { mutableStateOf((citaExistente?.duracion_min ?: if (tipoTerapia == "Fisica") 30 else 20).toString()) }
    var estado by remember { mutableStateOf(citaExistente?.estado ?: "Pendiente") }

    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    val datePicker = DatePickerDialog(context, { _, y, m, d ->
        calendar.set(y, m, d)
        fecha = dateFormatter.format(calendar.time)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        TimePickerDialog(context, { _, h, m ->
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, m)
            val selected = calendar.time
            onTimeSelected(timeFormatter.format(selected))

            val duracion = duracionMin.toIntOrNull() ?: 0
            calendar.add(Calendar.MINUTE, duracion)
            horaFin = timeFormatter.format(calendar.time)
        }, hour, minute, true).show()
    }

    fun hayConflictoDeHorario(): Boolean {
        val nuevaInicio = horaInicio
        val nuevaFin = horaFin

        return citasExistentes.any { cita ->
            cita.fecha == fecha &&
                    cita.tipo_terapia == tipoTerapia &&
                    cita.id_terapeuta == idTerapeuta &&
                    (
                            (nuevaInicio >= cita.hora_inicio && nuevaInicio < cita.hora_fin) ||
                                    (nuevaFin > cita.hora_inicio && nuevaFin <= cita.hora_fin) ||
                                    (nuevaInicio <= cita.hora_inicio && nuevaFin >= cita.hora_fin)
                            ) &&
                    (citaExistente == null || cita.id_cita != citaExistente.id_cita)
        }
    }

    fun recalcularHoraFin() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val inicio = sdf.parse(horaInicio)
            if (inicio != null) {
                val cal = Calendar.getInstance()
                cal.time = inicio
                cal.add(Calendar.MINUTE, duracionMin.toIntOrNull() ?: 0)
                horaFin = sdf.format(cal.time)
            }
        } catch (e: Exception) {
            // No hacer nada si horaInicio no es válida aún
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                if (citaExistente != null) "Editar Cita" else "Registrar Cita",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownTerapeuta(idTerapeuta, terapeutasDisponibles) { idTerapeuta = it }
                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                )
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de inicio", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker { horaInicio = it } }) {
                            Icon(Icons.Default.AccessTime, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                )
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de fin", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                )
                DropdownTipoTerapia(tipoTerapia) {
                    tipoTerapia = it
                    duracionMin = if (it == "Fisica") "30" else "20"
                    recalcularHoraFin()
                }
                DropdownEstado(estado) { estado = it }
                DropdownPaciente(idPaciente, pacientesDisponibles) { idPaciente = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (idPaciente == 0 || idTerapeuta == 0 || fecha.isBlank() || horaInicio.isBlank() || horaFin.isBlank() || duracionMin.isBlank()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Completa todos los campos obligatorios")
                    }
                    return@Button
                }

                if (hayConflictoDeHorario()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Ya existe una cita para este terapeuta en ese horario")
                    }
                    return@Button
                }

                val cita = CitaRequest(idPaciente, idTerapeuta, tipoTerapia, fecha, horaInicio, horaFin, estado, duracionMin.toInt())
                onCrear(cita)
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))) {
                Text(if (citaExistente != null) "Actualizar" else "Guardar", color = Color.White, fontFamily = poppins)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownTerapeuta(id: Int, items: List<Terapeuta>, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = items.find { it.id_terapeuta == id }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccione un terapeuta",
            onValueChange = {},
            readOnly = true,
            label = { Text("Terapeutas", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text("${it.nombre} ${it.apellido}", fontFamily = poppins) },
                    onClick = {
                        onSelect(it.id_terapeuta)
                        expanded = false
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownPaciente(id: Int, items: List<Paciente>, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = items.find { it.id_paciente == id }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccione un paciente",
            onValueChange = {},
            readOnly = true,
            label = { Text("Paciente", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text("${it.nombre} ${it.apellido}", fontFamily = poppins) },
                    onClick = {
                        onSelect(it.id_paciente)
                        expanded = false
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownTipoTerapia(selectedValue: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de terapia", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("Fisica", "Neurologica").forEach {
                DropdownMenuItem(
                    text = { Text(it, fontFamily = poppins) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownEstado(selectedValue: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Estado", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("Pendiente", "Confirmada", "Completada",  "Cancelada").forEach {
                DropdownMenuItem(
                    text = { Text(it, fontFamily = poppins) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    })
            }
        }
    }
}
