package com.example.clinicadiseo.Forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.clinicadiseo.Data_Models.Usuario
import com.example.clinicadiseo.Data_Models.UsuarioRequest
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioDialog(
    usuarioExistente: Usuario? = null,
    onDismiss: () -> Unit,
    onCrear: (UsuarioRequest) -> Unit,
    snackbarHostState: SnackbarHostState,
    mostrarPasswordPorDefecto: Boolean = false
) {
    var nombre by remember { mutableStateOf(usuarioExistente?.nombre ?: "") }
    var email by remember { mutableStateOf(usuarioExistente?.email ?: "") }
    var password by remember { mutableStateOf(usuarioExistente?.password ?: "") }
    var rol by remember { mutableStateOf(usuarioExistente?.rol ?: "Encargado") }
    var estado by remember { mutableStateOf(usuarioExistente?.estado ?: "Activo") }
    var showPassword by remember { mutableStateOf(usuarioExistente != null || mostrarPasswordPorDefecto) }

    val roles = listOf("Administrador", "Terapeuta", "Encargado")
    val estados = listOf("Activo", "Inactivo")
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                if (usuarioExistente != null) "Editar Usuario" else "Crear Usuario",
                fontFamily = poppinsbold,
                color = Color(0xFF1A5D1A),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    "Nombre" to nombre,
                    "Email" to email
                ).forEachIndexed { index, pair ->
                    val (label, value) = pair
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            when (index) {
                                0 -> nombre = it
                                1 -> email = it
                            }
                        },
                        label = { Text(label, fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                var expandedRol by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedRol,
                    onExpandedChange = { expandedRol = !expandedRol },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedRol, onDismissRequest = { expandedRol = false }) {
                        roles.forEach {
                            DropdownMenuItem(
                                text = { Text(it, fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    rol = it
                                    expandedRol = false
                                }
                            )
                        }
                    }
                }

                var expandedEstado by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado", fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedEstado, onDismissRequest = { expandedEstado = false }) {
                        estados.forEach {
                            DropdownMenuItem(
                                text = { Text(it, fontFamily = poppins, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    estado = it
                                    expandedEstado = false
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
                    if (password.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("La contraseña no puede estar vacía")
                        }
                        return@Button
                    }
                    val usuario = UsuarioRequest(nombre, email, password, rol, estado)
                    onCrear(usuario)
                    scope.launch {
                        snackbarHostState.showSnackbar("Usuario guardado correctamente")
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff1A5D1A)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    if (usuarioExistente != null) "Actualizar Usuario" else "Crear Usuario",
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