package com.example.clinicadiseo.pantallas

import Prestamo
import PrestamoResponse
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.Forms.CrearPrestamoDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFPrestamo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
fun PrestamosScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var search by remember { mutableStateOf("") }
    var prestamos by remember { mutableStateOf<List<Prestamo>>(emptyList()) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var prestamoAEditar by remember { mutableStateOf<Prestamo?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var prestamoSeleccionadoParaEliminar by remember { mutableStateOf<Prestamo?>(null) }
    var isButtonClicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isButtonClicked) 0.9f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    LaunchedEffect(isButtonClicked) {
        if (isButtonClicked) {
            delay(200)
            isButtonClicked = false
        }
    }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getPrestamos().enqueue(object : Callback<PrestamoResponse> {
            override fun onResponse(call: Call<PrestamoResponse>, response: Response<PrestamoResponse>) {
                if (response.isSuccessful) {
                    prestamos = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<PrestamoResponse>, t: Throwable) {
                loading = false
                Log.e("Prestamos", "Error: ${t.message}")
            }
        })

        RetrofitClient.instance.getPacientes().enqueue(object : Callback<PacienteResponse> {
            override fun onResponse(call: Call<PacienteResponse>, response: Response<PacienteResponse>) {
                if (response.isSuccessful) {
                    pacientes = response.body()?.result ?: emptyList()
                }
            }

            override fun onFailure(call: Call<PacienteResponse>, t: Throwable) {
                Log.e("Pacientes", "Error: ${t.message}")
            }
        })

        RetrofitClient.instance.getProductos().enqueue(object : Callback<ProductoResponse> {
            override fun onResponse(call: Call<ProductoResponse>, response: Response<ProductoResponse>) {
                if (response.isSuccessful) {
                    productos = response.body()?.result ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ProductoResponse>, t: Throwable) {
                Log.e("Productos", "Error: ${t.message}")
            }
        })
    }

    fun actualizarInventario(idProducto: Int, sumar: Boolean) {
        val producto = productos.find { it.id_producto == idProducto }
        if (producto != null) {
            val nuevaCantidad = if (sumar) producto.cantidad_disponible + 1 else producto.cantidad_disponible - 1
            val productoActualizado = producto.copy(cantidad_disponible = nuevaCantidad)
            RetrofitClient.instance.updateProducto(idProducto, productoActualizado)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        Log.d("Inventario", "Producto actualizado correctamente")
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("Inventario", "Error al actualizar el producto: ${t.message}")
                    }
                })
        }
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val prestamosFiltrados = prestamos.filter {
        val paciente = it.paciente
        paciente != null && (paciente.nombre.contains(search, true) || paciente.apellido.contains(search, true))
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
                        text = "Préstamos",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            isButtonClicked = true
                            prestamoAEditar = null
                            mostrarDialogo = true
                        },
                        containerColor = Color(0xFF1A5D1A),
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(46.dp)
                            .scale(scale)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar préstamo")
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar paciente", fontFamily = poppins, color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1A5D1A))
                    },
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
                        items(prestamosFiltrados) { prestamo ->
                            val estadoColor = if (prestamo.estado == "Devuelto") Color(0xFF2E7D32) else Color(0xFFD32F2F)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${prestamo.paciente?.nombre} ${prestamo.paciente?.apellido}",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Producto: ",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = prestamo.producto?.nombre ?: "Sin producto",
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Préstamo: ",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = prestamo.fecha_prestamo,
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Devolución: ",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = prestamo.fecha_devolucion ?: "Pendiente",
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                prestamo.estado,
                                                fontFamily = poppins,
                                                color = Color.White
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = estadoColor
                                        ),
                                        border = null,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                prestamoAEditar = prestamo
                                                mostrarDialogo = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Editar",
                                                tint = Color(0xFF2E7D32)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                prestamoSeleccionadoParaEliminar = prestamo
                                                showDeleteConfirmation = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color(0xFFD32F2F)
                                            )
                                        }
                                        IconButton(onClick = {
                                            generarYCompartirPDFPrestamo(context,prestamo)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Reporte Generado Exitosamente")
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

    if (mostrarDialogo) {
        CrearPrestamoDialog(
            pacientes = pacientes,
            productos = productos,
            prestamoExistente = prestamoAEditar,
            onCrear = { nuevoPrestamo ->
                val call = if (prestamoAEditar != null) {
                    val estadoAnterior = prestamoAEditar!!.estado
                    val nuevoEstado = nuevoPrestamo.estado

                    if (estadoAnterior == "Prestado" && nuevoEstado == "Devuelto") {
                        actualizarInventario(nuevoPrestamo.id_producto, true)
                    } else if (estadoAnterior == "Devuelto" && nuevoEstado == "Prestado") {
                        actualizarInventario(nuevoPrestamo.id_producto, false)
                    }

                    RetrofitClient.instance.updatePrestamos(prestamoAEditar!!.id_prestamo, nuevoPrestamo)
                } else {
                    if (nuevoPrestamo.estado == "Prestado") {
                        actualizarInventario(nuevoPrestamo.id_producto, false)
                    }
                    RetrofitClient.instance.insertPrestamos(nuevoPrestamo)
                }

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        mostrarDialogo = false
                        prestamoAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Préstamo guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("GuardarPrestamo", "Error: ${t.message}")
                    }
                })
            },
            onDismiss = {
                mostrarDialogo = false
                prestamoAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    "¿Eliminar préstamo?",
                    fontFamily = poppinsbold,
                    color = Color(0xFF1A5D1A),
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "Esta acción no se puede deshacer",
                    fontFamily = poppins
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        prestamoSeleccionadoParaEliminar?.let {
                            if (it.estado == "Prestado") {
                                actualizarInventario(it.id_producto, true)
                            }
                            RetrofitClient.instance.deletePrestamos(it.id_prestamo)
                                .enqueue(object : Callback<ApiResponse> {
                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                        cargarDatos()
                                        CoroutineScope(Dispatchers.Main).launch {
                                            snackbarHostState.showSnackbar("Préstamo eliminado exitosamente")
                                        }
                                    }
                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                        Log.e("EliminarPrestamo", "Error: ${t.message}")
                                    }
                                })
                        }
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
                ) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}