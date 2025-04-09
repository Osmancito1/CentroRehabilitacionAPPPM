package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Encargado
import com.example.clinicadiseo.Data_Models.EncargadoRequest
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEncargadoDialog(
    encargadoExistente: Encargado? = null,
    onCrear: (EncargadoRequest) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf(encargadoExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(encargadoExistente?.apellido ?: "") }
    var telefono by remember { mutableStateOf(encargadoExistente?.telefono ?: "") }
    var direccion by remember { mutableStateOf(encargadoExistente?.direccion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (encargadoExistente == null) "Nuevo Encargado" else "Editar Encargado",
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank() || apellido.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos requeridos")
                        }
                        return@Button
                    }

                    val nuevoEncargado = EncargadoRequest(
                        nombre = nombre,
                        apellido = apellido,
                        telefono = telefono,
                        direccion = direccion
                    )

                    onCrear(nuevoEncargado)
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
