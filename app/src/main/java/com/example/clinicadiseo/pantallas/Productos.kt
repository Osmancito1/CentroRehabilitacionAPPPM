package com.example.clinicadiseo.pantallas

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
import com.example.clinicadiseo.Forms.CrearProductoDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFProducto
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
fun ProductosScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var loading by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var productoSeleccionadoParaEliminar by remember { mutableStateOf<Producto?>(null) }

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
        RetrofitClient.instance.getProductos().enqueue(object : Callback<ProductoResponse> {
            override fun onResponse(call: Call<ProductoResponse>, response: Response<ProductoResponse>) {
                productos = response.body()?.result ?: emptyList()
                loading = false
            }

            override fun onFailure(call: Call<ProductoResponse>, t: Throwable) {
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) { cargarDatos() }

    val productosFiltrados = productos.filter {
        it.nombre.contains(search, ignoreCase = true)
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
                        text = "Productos",
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
                                isButtonClicked = true
                                productoAEditar = null
                                showDialog = true
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp).scale(scale)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }


                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        // Generate the PDF report for all products
                                        generarYCompartirPDFProducto(context, productos)
                                        snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Error al generar reporte: ${e.message}")
                                    }
                                }
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Generar Reporte PDF")
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar producto", fontFamily = poppins) },
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
                        items(productosFiltrados) { producto ->
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
                                        text = producto.nombre,
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Row {
                                        Text("Descripción: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(producto.descripcion, fontFamily = poppins, color = Color.Black)
                                    }

                                    Row {
                                        Text("Categoría: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(producto.categoria, fontFamily = poppins, color = Color.Black)
                                    }

                                    Row {
                                        Text("Cantidad Disponible: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(producto.cantidad_disponible.toString(), fontFamily = poppins, color = Color.Black)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            productoAEditar = producto
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            showDialogConfirmacion = true
                                            productoSeleccionadoParaEliminar = producto
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
        CrearProductoDialog(
            productoExistente = productoAEditar,
            onCrear = { nuevoProducto ->
                val productoCompleto = Producto(
                    id_producto = productoAEditar?.id_producto ?: 0,
                    nombre = nuevoProducto.nombre,
                    descripcion = nuevoProducto.descripcion,
                    categoria = nuevoProducto.categoria,
                    cantidad_disponible = nuevoProducto.cantidad_disponible
                )

                val call = if (productoAEditar != null)
                    RetrofitClient.instance.updateProducto(productoAEditar!!.id_producto, productoCompleto)
                else
                    RetrofitClient.instance.insertProducto(productoCompleto)

                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        productoAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Producto guardado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                })
            },
            onDismiss = {
                showDialog = false
                productoAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && productoSeleccionadoParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text("¿Eliminar producto?", fontFamily = poppinsbold, color = Color(0xFF1A5D1A), fontSize = 20.sp)
            },
            confirmButton = {
                Button(onClick = {
                    RetrofitClient.instance.deleteProducto(productoSeleccionadoParaEliminar!!.id_producto)
                        .enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                cargarDatos()
                                showDialogConfirmacion = false
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar("Encargado eliminado exitosamente")
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
