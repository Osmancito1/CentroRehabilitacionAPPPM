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
import com.example.clinicadiseo.Forms.CrearEncargadoDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFEncargado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
fun EncargadosScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    var search by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var encargados by remember { mutableStateOf<List<Encargado>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var encargadoAEditar by remember { mutableStateOf<Encargado?>(null) }
    var loading by remember { mutableStateOf(true) }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var encargadoSeleccionadoParaEliminar by remember { mutableStateOf<Encargado?>(null) }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getEncargados().enqueue(object : Callback<EncargadoResponse> {
            override fun onResponse(call: Call<EncargadoResponse>, response: Response<EncargadoResponse>) {
                if (response.isSuccessful) {
                    encargados = response.body()?.result ?: emptyList()
                }
                loading = false
            }
            override fun onFailure(call: Call<EncargadoResponse>, t: Throwable) {
                Log.e("EncargadosScreen", "Error: ${t.message}")
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) { cargarDatos() }

    val encargadosFiltrados = encargados.filter {
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
                        text = "Encargados",
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
                                encargadoAEditar = null
                                showDialog = true
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar encargado")
                        }

                        FloatingActionButton(
                            onClick = {
                                try {
                                    if (encargadosFiltrados.isNotEmpty()) {
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) {
                                                generarYCompartirPDFEncargado(context, encargadosFiltrados)
                                            }
                                            snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("No hay encargados para generar reporte")
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
                            Icon(Icons.Default.Print, contentDescription = "Reporte Encargado")
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar encargado", fontFamily = poppins) },
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
                        items(encargadosFiltrados) { encargado ->
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
                                        text = "${encargado.nombre} ${encargado.apellido}",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    Row {
                                        Text("Teléfono: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = encargado.telefono ?: "Sin número",
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row {
                                        Text("Dirección: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(
                                            text = encargado.direccion ?: "Sin dirección",
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            encargadoAEditar = encargado
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            encargadoSeleccionadoParaEliminar = encargado
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
        CrearEncargadoDialog(
            encargadoExistente = encargadoAEditar,
            onCrear = { nuevoEncargado ->
                val call = if (encargadoAEditar != null) {
                    val encargadoCompleto = Encargado(
                        id_encargado = encargadoAEditar!!.id_encargado,
                        nombre = nuevoEncargado.nombre,
                        apellido = nuevoEncargado.apellido,
                        telefono = nuevoEncargado.telefono,
                        direccion = nuevoEncargado.direccion
                    )
                    RetrofitClient.instance.updateEncargado(encargadoAEditar!!.id_encargado, encargadoCompleto)
                } else {
                    val encargadoCompleto = Encargado(
                        id_encargado = 0,
                        nombre = nuevoEncargado.nombre,
                        apellido = nuevoEncargado.apellido,
                        telefono = nuevoEncargado.telefono,
                        direccion = nuevoEncargado.direccion
                    )
                    RetrofitClient.instance.insertEncargado(encargadoCompleto)
                }

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        encargadoAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Encargado guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("GuardarEncargado", "Error: ${t.message}")
                    }
                })
            },
            onDismiss = {
                showDialog = false
                encargadoAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && encargadoSeleccionadoParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text(
                    text = "¿Estás seguro de eliminar este encargado?",
                    fontFamily = poppinsbold,
                    color = Color(0xFF1A5D1A),
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        encargadoSeleccionadoParaEliminar?.let { encargado ->
                            RetrofitClient.instance.deleteEncargado(encargado.id_encargado)
                                .enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                        if (response.isSuccessful) {
                                            cargarDatos()
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar("Encargado eliminado exitosamente")
                                            }
                                        }
                                        showDialogConfirmacion = false
                                    }

                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                        Log.e("EliminarEncargado", "Error: ${t.message}")
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