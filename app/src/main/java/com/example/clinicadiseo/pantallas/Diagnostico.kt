package com.example.clinicadiseo.pantallas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.Forms.CrearDiagnosticoDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

@Composable
fun DiagnosticosScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var diagnosticos by remember { mutableStateOf<List<Diagnostico>>(emptyList()) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var terapeutas by remember { mutableStateOf<List<Terapeuta>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var diagnosticoAEditar by remember { mutableStateOf<Diagnostico?>(null) }
    var loading by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var diagnosticoSeleccionadoParaEliminar by remember { mutableStateOf<Diagnostico?>(null) }

    var isButtonClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isButtonClicked) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200)
    )
    LaunchedEffect(isButtonClicked) {
        if (isButtonClicked) {
            kotlinx.coroutines.delay(200)
            isButtonClicked = false
        }
    }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getDiagnosticos().enqueue(object : Callback<DiagnosticoResponse> {
            override fun onResponse(call: Call<DiagnosticoResponse>, response: Response<DiagnosticoResponse>) {
                diagnosticos = response.body()?.result?.map { diagnostico ->
                    diagnostico.copy(
                        id_paciente = diagnostico.paciente?.let { paciente ->
                            pacientes.find { it.nombre == paciente.nombre && it.apellido == paciente.apellido }?.id_paciente
                                ?: 0
                        } ?: 0,
                        id_terapeuta = diagnostico.terapeuta?.let { terapeuta ->
                            terapeutas.find { it.nombre == terapeuta.nombre && it.especialidad == terapeuta.especialidad }?.id_terapeuta
                                ?: 0
                        } ?: 0
                    )
                } ?: emptyList()
                loading = false
            }

            override fun onFailure(call: Call<DiagnosticoResponse>, t: Throwable) {
                loading = false
            }
        })



        RetrofitClient.instance.getPacientes().enqueue(object : Callback<PacienteResponse> {
            override fun onResponse(call: Call<PacienteResponse>, response: Response<PacienteResponse>) {
                pacientes = response.body()?.result ?: emptyList()
            }
            override fun onFailure(call: Call<PacienteResponse>, t: Throwable) {}
        })

        RetrofitClient.instance.getTerapeutas().enqueue(object : Callback<TerapeutaResponse> {
            override fun onResponse(call: Call<TerapeutaResponse>, response: Response<TerapeutaResponse>) {
                terapeutas = response.body()?.result ?: emptyList()
            }
            override fun onFailure(call: Call<TerapeutaResponse>, t: Throwable) {}
        })
    }

    LaunchedEffect(Unit) { cargarDatos() }

    val diagnosticosFiltrados = diagnosticos.filter {
        val nombrePaciente = "${it.paciente?.nombre ?: ""} ${it.paciente?.apellido ?: ""}"
        nombrePaciente.contains(search, ignoreCase = true)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Diagnósticos",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            isButtonClicked = true
                            diagnosticoAEditar = null
                            showDialog = true
                        },
                        containerColor = Color(0xFF1A5D1A),
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(46.dp)
                            .scale(scale)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar diagnóstico")
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

                Spacer(modifier = Modifier.height(16.dp))

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A5D1A))
                    }
                } else {
                    LazyColumn {
                        items(diagnosticosFiltrados) { diagnostico ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${diagnostico.paciente?.nombre ?: ""} ${diagnostico.paciente?.apellido ?: ""}",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Terapeuta: ${diagnostico.terapeuta?.nombre ?: ""} - ${diagnostico.terapeuta?.especialidad ?: ""}",
                                        fontFamily = poppinsbold,
                                        fontSize = 14.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Tratamiento:",
                                        fontFamily = poppinsbold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = diagnostico.tratamiento,
                                        fontFamily = poppins,
                                        fontSize = 14.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Descripción:",
                                        fontFamily = poppinsbold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = diagnostico.descripcion ?: "",
                                        fontFamily = poppins,
                                        fontSize = 14.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            if (pacientes.isNotEmpty() && terapeutas.isNotEmpty()) {
                                                println("PACIENTES: ${pacientes.map { it.id_paciente }}") // o Log.d(...)
                                                println("TERAPEUTAS: ${terapeutas.map { it.id_terapeuta }}")
                                                println("EDITANDO A: ${diagnostico.paciente?.id_paciente} - ${diagnostico.terapeuta?.id_terapeuta}")

                                                diagnosticoAEditar = diagnostico
                                                showDialog = true
                                            } else {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Espere mientras se cargan los datos...")
                                                }
                                            }
                                        })
                                        {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            diagnosticoSeleccionadoParaEliminar = diagnostico
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                                        }
                                        IconButton(onClick = {
                                            generarYCompartirPDF(context, diagnostico)
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

    if (showDialog && pacientes.isNotEmpty() && terapeutas.isNotEmpty()) {
        CrearDiagnosticoDialog(
            diagnosticoExistente = diagnosticoAEditar,
            pacientesDisponibles = pacientes,
            terapeutasDisponibles = terapeutas,
            onCrear = { nuevo ->
                val call = if (diagnosticoAEditar != null)
                    RetrofitClient.instance.updateDiagnostico(diagnosticoAEditar!!.id_diagnostico, nuevo)
                else
                    RetrofitClient.instance.insertDiagnostico(nuevo)

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        diagnosticoAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Diagnóstico guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                })
            },
            onDismiss = {
                showDialog = false
                diagnosticoAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }


    if (showDialogConfirmacion && diagnosticoSeleccionadoParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text("¿Eliminar diagnóstico?", fontFamily = poppinsbold, color = Color(0xFF1A5D1A), fontSize = 20.sp)
            },
            confirmButton = {
                Button(onClick = {
                    RetrofitClient.instance.deleteDiagnostico(diagnosticoSeleccionadoParaEliminar!!.id_diagnostico)
                        .enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                cargarDatos()
                                showDialogConfirmacion = false
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar("Diagnóstico eliminado exitosamente")
                                }
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
