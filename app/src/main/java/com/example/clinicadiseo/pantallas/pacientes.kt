package com.example.clinicadiseo.pantallas

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.Forms.CrearPacienteDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.clinicadiseo.Reports.generarYCompartirPDFPacientes

private fun SnackbarDuration.toMillis(): Long {
    return when (this) {
        SnackbarDuration.Short -> 4000L
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Indefinite -> 3000L
    }
}

@Composable
fun PacientesScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var encargados by remember { mutableStateOf<List<Encargado>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var pacienteAEditar by remember { mutableStateOf<Paciente?>(null) }
    var loading by remember { mutableStateOf(true) }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var pacienteSeleccionadoParaEliminar by remember { mutableStateOf<Paciente?>(null) }

    fun cargarDatos() {
        loading = true

        RetrofitClient.instance.getPacientes().enqueue(object : Callback<PacienteResponse> {
            override fun onResponse(call: Call<PacienteResponse>, response: Response<PacienteResponse>) {
                if (response.isSuccessful) {
                    pacientes = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<PacienteResponse>, t: Throwable) {
                Log.e("PacientesScreen", "Error: ${t.message}")
                loading = false
            }
        })

        RetrofitClient.instance.getEncargados().enqueue(object : Callback<EncargadoResponse> {
            override fun onResponse(call: Call<EncargadoResponse>, response: Response<EncargadoResponse>) {
                if (response.isSuccessful) {
                    encargados = response.body()?.result ?: emptyList()
                }
            }

            override fun onFailure(call: Call<EncargadoResponse>, t: Throwable) {
                Log.e("Encargados", "Error: ${t.message}")
            }
        })
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val pacientesFiltrados = pacientes.filter {
        it.nombre.contains(search, ignoreCase = true) || it.apellido.contains(search, ignoreCase = true)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                var isSnackbarVisible by remember { mutableStateOf(true) }
                val scale by animateFloatAsState(
                    targetValue = if (isSnackbarVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 300)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .scale(scale),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A5D1A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            color = Color.White,
                            fontFamily = poppinsbold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                LaunchedEffect(snackbarData) {
                    kotlinx.coroutines.delay(snackbarData.visuals.duration.toMillis())
                    isSnackbarVisible = false
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8F5E9))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pacientes",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                pacienteAEditar = null
                                showDialog = true
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar paciente")
                        }

                        FloatingActionButton(
                            onClick = {
                                try {
                                    if (pacientesFiltrados.isNotEmpty()) {
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) {
                                                generarYCompartirPDFPacientes(context, pacientesFiltrados)
                                            }
                                            snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("No hay pacientes para generar reporte")
                                        }
                                    }
                                } catch (e: Exception) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error al generar reporte: ${e.message}")
                                    }
                                    Log.e("PDF_Error", "Error al generar PDF", e)
                                }
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Reporte Paciente")
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar paciente", fontFamily = poppins) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1A5D1A)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xff1A5D1A),
                        focusedLabelColor = Color(0xff1A5D1A),
                        cursorColor = Color(0xff1A5D1A)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A5D1A))
                    }
                } else {
                    LazyColumn {
                        items(pacientesFiltrados) { paciente ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${paciente.nombre} ${paciente.apellido}",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A)
                                    )

                                    Row {
                                        Text("Fecha nacimiento: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = paciente.fecha_nacimiento.substring(0, 10),
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row {
                                        Text("Teléfono: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = paciente.telefono,
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row {
                                        Text("Dirección: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = paciente.direccion,
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row {
                                        Text("Encargado: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = paciente.encargado?.let { "${it.nombre} ${it.apellido}" } ?: "No asignado",
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            pacienteAEditar = paciente
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            pacienteSeleccionadoParaEliminar = paciente
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CrearPacienteDialog(
            pacienteExistente = pacienteAEditar,
            encargadosDisponibles = encargados,
            onCrear = { nuevoPaciente ->
                val call = if (pacienteAEditar != null) {
                    RetrofitClient.instance.updatePaciente(pacienteAEditar!!.id_paciente, nuevoPaciente)
                } else {
                    RetrofitClient.instance.insertPaciente(nuevoPaciente)
                }

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        showDialog = false
                        pacienteAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Paciente guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("GuardarPaciente", "Error: ${t.message}")
                    }
                })
            },
            onDismiss = {
                showDialog = false
                pacienteAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && pacienteSeleccionadoParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text(
                    text = "¿Estás seguro de eliminar este paciente?",
                    fontFamily = poppinsbold,
                    color = Color(0xFF1A5D1A),
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pacienteSeleccionadoParaEliminar?.let { paciente ->
                            RetrofitClient.instance.deletePaciente(paciente.id_paciente)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            cargarDatos()
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar("Paciente eliminado exitosamente")
                                            }
                                        }
                                        showDialogConfirmacion = false
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Log.e("EliminarPaciente", "Error: ${t.message}")
                                    }
                                })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogConfirmacion = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}