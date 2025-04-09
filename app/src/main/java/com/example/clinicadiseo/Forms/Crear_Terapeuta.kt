package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Terapeuta
import com.example.clinicadiseo.Data_Models.TerapeutaRequest
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTerapeutaDialog(
    terapeutaExistente: Terapeuta? = null,
    onCrear: (TerapeutaRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(terapeutaExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(terapeutaExistente?.apellido ?: "") }
    var especialidad by remember { mutableStateOf(terapeutaExistente?.especialidad ?: "") }
    var telefono by remember { mutableStateOf(terapeutaExistente?.telefono ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (terapeutaExistente == null) "Nuevo Terapeuta" else "Editar Tepareuta",
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
                listOf(
                    "Nombre" to nombre,
                    "Apellido" to apellido,
                    "Especialidad" to especialidad,
                    "Teléfono" to telefono
                ).forEachIndexed { index, pair ->
                    val (label, value) = pair
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            when (index) {
                                0 -> nombre = it
                                1 -> apellido = it
                                2 -> especialidad = it
                                3 -> telefono = it
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank() || apellido.isBlank() || especialidad.isBlank() || telefono.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos")
                        }
                        return@Button
                    }
                    val nuevo = TerapeutaRequest(nombre, apellido, especialidad, telefono)
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