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
import androidx.compose.material.icons.filled.*
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
import com.example.clinicadiseo.Forms.CrearCitaDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPdfCitas
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun SnackbarDuration.toMillis(): Long {
    return when (this) {
        SnackbarDuration.Short -> 4000L
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Indefinite -> 3000L
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var search by remember { mutableStateOf("") }
    var filtroEstado by remember { mutableStateOf("Todos") }
    var citas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var terapeutas by remember { mutableStateOf<List<Terapeuta>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var citaAEditar by remember { mutableStateOf<Cita?>(null) }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var citaSeleccionadaParaEliminar by remember { mutableStateOf<Cita?>(null) }
    var loading by remember { mutableStateOf(true) }
    var expandedEstado by remember { mutableStateOf(false) }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getCitas().enqueue(object : Callback<CitaResponse> {
            override fun onResponse(call: Call<CitaResponse>, response: Response<CitaResponse>) {
                if (response.isSuccessful) {
                    citas = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<CitaResponse>, t: Throwable) {
                loading = false
            }
        })

        RetrofitClient.instance.getPacientes().enqueue(object : Callback<PacienteResponse> {
            override fun onResponse(call: Call<PacienteResponse>, response: Response<PacienteResponse>) {
                if (response.isSuccessful) pacientes = response.body()?.result ?: emptyList()
            }

            override fun onFailure(call: Call<PacienteResponse>, t: Throwable) {}
        })

        RetrofitClient.instance.getTerapeutas().enqueue(object : Callback<TerapeutaResponse> {
            override fun onResponse(call: Call<TerapeutaResponse>, response: Response<TerapeutaResponse>) {
                if (response.isSuccessful) terapeutas = response.body()?.result ?: emptyList()
            }

            override fun onFailure(call: Call<TerapeutaResponse>, t: Throwable) {}
        })
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val citasFiltradas = citas.filter {
        val nombrePaciente = "${it.paciente?.nombre ?: ""} ${it.paciente?.apellido ?: ""}"
        val coincideBusqueda = nombrePaciente.contains(search, ignoreCase = true)
        val coincideEstado = filtroEstado == "Todos" || it.estado == filtroEstado
        coincideBusqueda && coincideEstado
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
                            fontSize = 15.sp,
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Citas",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )
                    FloatingActionButton(
                        onClick = {
                            citaAEditar = null
                            showDialog = true
                        },
                        containerColor = Color(0xFF1A5D1A),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar cita")
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar por paciente", fontFamily = poppins) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1A5D1A)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        focusedLabelColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado }
                ) {
                    OutlinedTextField(
                        value = filtroEstado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado", fontFamily = poppins, color = Color(0xFF1A5D1A)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A))
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {
                        listOf("Todos", "Pendiente", "Confirmada", "Completada", "Cancelada").forEach {
                            DropdownMenuItem(
                                text = { Text(it, fontFamily = poppins) },
                                onClick = {
                                    filtroEstado = it
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        items(citasFiltradas) { cita ->
                            val estadoColor = when (cita.estado) {
                                "Pendiente" -> Color(0xFFFFA000)
                                "Cancelada" -> Color(0xFFD32F2F)
                                else -> Color(0xFF1A5D1A)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${cita.paciente?.nombre ?: ""} ${cita.paciente?.apellido ?: ""}",
                                        fontFamily = poppinsbold,
                                        fontSize = 20.sp,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Column {
                                        Row {
                                            Text("Terapeuta: ", fontFamily = poppinsbold, color = Color.Black)
                                            Text(
                                                text = "${cita.terapeuta?.nombre ?: "Desconocido"} ${cita.terapeuta?.apellido ?: ""}",
                                                fontFamily = poppins,
                                                color = Color.Black
                                            )
                                        }

                                        Row {
                                            Text("Fecha: ", fontFamily = poppinsbold, color = Color.Black)
                                            Text(cita.fecha, fontFamily = poppins, color = Color.Black)
                                        }

                                        Row {
                                            Text("Hora: ", fontFamily = poppinsbold, color = Color.Black)
                                            Text("${cita.hora_inicio} - ${cita.hora_fin}", fontFamily = poppins, color = Color.Black)
                                        }

                                        Row {
                                            Text("Tipo: ", fontFamily = poppinsbold, color = Color.Black)
                                            Text(cita.tipo_terapia, fontFamily = poppins, color = Color.Black)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .background(estadoColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = cita.estado,
                                            fontFamily = poppinsbold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            citaAEditar = cita
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }
                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            citaSeleccionadaParaEliminar = cita
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                                        }
                                        IconButton(onClick = {
                                            generarYCompartirPdfCitas(context, cita)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                            }
                                        }) {
                                            Icon(Icons.Default.Share, contentDescription = "Compartir PDF", tint = Color(0xFF1565C0))
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
        CrearCitaDialog(
            citaExistente = citaAEditar,
            pacientesDisponibles = pacientes,
            terapeutasDisponibles = terapeutas,
            citasExistentes = citas,
            onCrear = { nuevaCita ->
                val call = if (citaAEditar != null)
                    RetrofitClient.instance.updateCita(citaAEditar!!.id_cita, nuevaCita)
                else
                    RetrofitClient.instance.insertCita(nuevaCita)

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        citaAEditar = null
                        cargarDatos()
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("GuardarCita", "Error: ${t.message}")
                    }
                })
            },
            onDismiss = {
                showDialog = false
                citaAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && citaSeleccionadaParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text("¿Eliminar esta cita?", fontFamily = poppinsbold, color = Color(0xFF1A5D1A))
            },
            confirmButton = {
                Button(onClick = {
                    RetrofitClient.instance.deleteCita(citaSeleccionadaParaEliminar!!.id_cita)
                        .enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                cargarDatos()
                                showDialogConfirmacion = false
                            }

                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                        })
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialogConfirmacion = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}
