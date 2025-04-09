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
import com.example.clinicadiseo.Forms.CrearTerapeutaDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFTerapeutas
import kotlinx.coroutines.*
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
fun TerapeutasScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    var search by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var terapeutas by remember { mutableStateOf<List<Terapeuta>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var terapeutaAEditar by remember { mutableStateOf<Terapeuta?>(null) }
    var loading by remember { mutableStateOf(true) }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var terapeutaSeleccionadoParaEliminar by remember { mutableStateOf<Terapeuta?>(null) }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getTerapeutas().enqueue(object : Callback<TerapeutaResponse> {
            override fun onResponse(call: Call<TerapeutaResponse>, response: Response<TerapeutaResponse>) {
                if (response.isSuccessful) {
                    terapeutas = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<TerapeutaResponse>, t: Throwable) {
                Log.e("TerapeutasScreen", "Error: ${t.message}")
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) { cargarDatos() }

    val terapeutasFiltrados = terapeutas.filter {
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
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                LaunchedEffect(snackbarData) {
                    delay(snackbarData.visuals.duration.toMillis())
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
                        text = "Terapeutas",
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
                                terapeutaAEditar = null
                                showDialog = true
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar terapeuta")
                        }

                        FloatingActionButton(
                            onClick = {
                                try {
                                    if (terapeutasFiltrados.isNotEmpty()) {
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) {
                                                generarYCompartirPDFTerapeutas(context, terapeutasFiltrados)
                                            }
                                            snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("No hay terapeutas para generar reporte")
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
                            Icon(Icons.Default.Print, contentDescription = "Reporte Terapeuta")
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar terapeuta", fontFamily = poppins) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1A5D1A)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        focusedLabelColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A5D1A))
                    }
                } else {
                    LazyColumn {
                        items(terapeutasFiltrados) { terapeuta ->
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
                                        text = "${terapeuta.nombre} ${terapeuta.apellido}",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    Row {
                                        Text("Teléfono: ", fontFamily = poppins, color = Color.Black)
                                        Text(
                                            text = terapeuta.telefono ?: "Sin número",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                    }

                                    Row {
                                        Text("Especialidad: ", fontFamily = poppins, color = Color.Black)
                                        Text(
                                            text = terapeuta.especialidad ?: "Sin especialidad",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            terapeutaAEditar = terapeuta
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            terapeutaSeleccionadoParaEliminar = terapeuta
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
        CrearTerapeutaDialog(
            terapeutaExistente = terapeutaAEditar,
            onCrear = { nuevoTerapeuta ->
                val call = if (terapeutaAEditar != null) {
                    val terapeutaCompleto = Terapeuta(
                        id_terapeuta = terapeutaAEditar!!.id_terapeuta,
                        nombre = nuevoTerapeuta.nombre,
                        apellido = nuevoTerapeuta.apellido,
                        telefono = nuevoTerapeuta.telefono,
                        especialidad = nuevoTerapeuta.especialidad
                    )
                    RetrofitClient.instance.updateTerapeuta(terapeutaAEditar!!.id_terapeuta, terapeutaCompleto)
                } else {
                    val terapeutaCompleto = Terapeuta(
                        id_terapeuta = 0,
                        nombre = nuevoTerapeuta.nombre,
                        apellido = nuevoTerapeuta.apellido,
                        telefono = nuevoTerapeuta.telefono,
                        especialidad = nuevoTerapeuta.especialidad
                    )
                    RetrofitClient.instance.insertTerapeuta(terapeutaCompleto)
                }

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        terapeutaAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Terapeuta guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("GuardarTerapeuta", "Error: ${t.message}")
                    }
                })
            },
            onDismiss = {
                showDialog = false
                terapeutaAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && terapeutaSeleccionadoParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text(
                    text = "¿Estás seguro de eliminar este terapeuta?",
                    fontFamily = poppinsbold,
                    color = Color(0xFF1A5D1A),
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        terapeutaSeleccionadoParaEliminar?.let { terapeuta ->
                            RetrofitClient.instance.deleteTerapeuta(terapeuta.id_terapeuta)
                                .enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                        if (response.isSuccessful) {
                                            cargarDatos()
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar("Terapeuta eliminado exitosamente")
                                            }
                                        }
                                        showDialogConfirmacion = false
                                    }

                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                        Log.e("EliminarTerapeuta", "Error: ${t.message}")
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