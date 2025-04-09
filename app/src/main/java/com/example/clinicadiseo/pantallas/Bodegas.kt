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
import androidx.compose.material.icons.filled.Outbox
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
import com.example.clinicadiseo.Forms.CrearBodegaDialog
import com.example.clinicadiseo.Forms.SacarProductoDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFBodega
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
fun BodegasScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    var bodegas by remember { mutableStateOf<List<Bodega>>(emptyList()) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var bodegaAEditar by remember { mutableStateOf<Bodega?>(null) }
    var loading by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }
    var showDialogConfirmacion by remember { mutableStateOf(false) }
    var bodegaSeleccionadaParaEliminar by remember { mutableStateOf<Bodega?>(null) }
    var showDialogSacarProducto by remember { mutableStateOf(false) }
    var bodegaSeleccionadaParaSacar by remember { mutableStateOf<Bodega?>(null) }


    fun cargarDatos() {
        loading = true


        RetrofitClient.instance.getProductos().enqueue(object : Callback<ProductoResponse> {
            override fun onResponse(call: Call<ProductoResponse>, response: Response<ProductoResponse>) {
                if (response.isSuccessful) {
                    productos = response.body()?.result ?: emptyList()
                    RetrofitClient.instance.getBodegas().enqueue(object : Callback<BodegaResponse> {
                        override fun onResponse(call: Call<BodegaResponse>, response: Response<BodegaResponse>) {
                            if (response.isSuccessful) {
                                bodegas = response.body()?.result ?: emptyList()
                            }
                            loading = false
                        }
                        override fun onFailure(call: Call<BodegaResponse>, t: Throwable) {
                            loading = false
                        }
                    })
                }
            }
            override fun onFailure(call: Call<ProductoResponse>, t: Throwable) {
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val bodegasFiltradas = bodegas.filter {
        val nombreProducto = it.producto?.nombre ?: ""
        nombreProducto.contains(search, ignoreCase = true)
    }

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
                        text = "Bodega",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FloatingActionButton(
                            onClick = {
                                isButtonClicked = true
                                bodegaAEditar = null
                                showDialog = true
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(46.dp)
                                .scale(scale)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }

                        FloatingActionButton(
                            onClick = {
                                try {
                                    if (bodegasFiltradas.isNotEmpty()) {
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) {
                                                generarYCompartirPDFBodega(context, bodegasFiltradas)
                                            }
                                            snackbarHostState.showSnackbar("Reporte Generado con Éxito")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("No hay bodegas para generar reporte")
                                        }
                                    }
                                } catch (e: Exception) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error al generar reporte: ${e.message}")
                                    }
                                }
                            },
                            containerColor = Color(0xFF1A5D1A),
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Reporte Bodega")
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = {
                        Text("Buscar por producto", fontFamily = poppins, color = Color.Gray)
                    },
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
                        items(bodegasFiltradas) { bodega ->
                            val cantidadColor = when {
                                bodega.cantidad < 5 -> Color.Red
                                bodega.cantidad in 5..12 -> Color(0xFFFFA000)
                                else -> Color(0xFF388E3C)
                            }

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
                                        text = bodega.producto?.nombre ?: "Sin nombre",
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Cantidad: ",
                                            fontFamily = poppinsbold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = bodega.cantidad.toString(),
                                            fontFamily = poppins,
                                            color = Color.Black
                                        )
                                    }

                                    bodega.ubicacion?.let {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Ubicación: ",
                                                fontFamily = poppinsbold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = it,
                                                fontFamily = poppins,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                bodegaAEditar = bodega
                                                showDialog = true
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
                                                showDialogConfirmacion = true
                                                bodegaSeleccionadaParaEliminar = bodega
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color(0xFFD32F2F)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                bodegaSeleccionadaParaSacar = bodega
                                                showDialogSacarProducto = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Outbox,
                                                contentDescription = "Sacar",
                                                tint = Color(0xFF1A237E)
                                            )
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
        CrearBodegaDialog(
            bodegaExistente = bodegaAEditar,
            productosDisponibles = productos,
            onCrear = { nuevo ->
                val call = if (bodegaAEditar != null)
                    RetrofitClient.instance.updateBodegas(bodegaAEditar!!.id_bodega, nuevo)
                else
                    RetrofitClient.instance.insertBodegas(nuevo)
                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        showDialog = false
                        bodegaAEditar = null
                        cargarDatos()
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Registro de bodega creado exitosamente")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                })
            },
            onDismiss = {
                showDialog = false
                bodegaAEditar = null
            },
            snackbarHostState = snackbarHostState
        )
    }

    if (showDialogConfirmacion && bodegaSeleccionadaParaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialogConfirmacion = false },
            title = {
                Text(
                    "¿Eliminar registro de bodega?",
                    fontFamily = poppinsbold,
                    color = Color(0xFF1A5D1A),
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        RetrofitClient.instance.deleteBodegas(bodegaSeleccionadaParaEliminar!!.id_bodega)
                            .enqueue(object : Callback<ApiResponse> {
                                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                    cargarDatos()
                                    showDialogConfirmacion = false
                                    CoroutineScope(Dispatchers.Main).launch {
                                        snackbarHostState.showSnackbar("Registro de bodega eliminado exitosamente")
                                    }
                                }

                                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                            })
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
    if (showDialogSacarProducto && bodegaSeleccionadaParaSacar != null) {
        SacarProductoDialog(
            bodegaSeleccionada = bodegaSeleccionadaParaSacar,
            onSacarProducto = { cantidadSacada ->
                val bodega = bodegaSeleccionadaParaSacar!!
                val producto = bodega.producto!!

                println("🔄 ID de la bodega: ${bodega.id_bodega}")
                println("🔄 ID del producto asociado: ${producto.id_producto}")
                println("🔄 Nombre del producto: ${producto.nombre}")

                val nuevaCantidadBodega = bodega.cantidad - cantidadSacada

                val bodegaActualizada = BodegaRequest(
                    id_producto = bodega.id_producto,
                    cantidad = nuevaCantidadBodega,
                    ubicacion = bodega.ubicacion
                )

                RetrofitClient.instance.updateBodegas(
                    bodega.id_bodega,
                    bodegaActualizada
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        println("Bodega actualizada")
                        cargarDatos()
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        println("Error al actualizar bodega: ${t.message}")
                    }
                })

                val nuevoStockProducto = producto.cantidad_disponible + cantidadSacada

                val productoStockUpdate = ProductoStockUpdate(
                    cantidad_a_sumar = cantidadSacada
                )

                RetrofitClient.instance.updateProductoStock(
                    producto.id_producto,
                    productoStockUpdate
                ).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        println("Producto actualizado (stock: $nuevoStockProducto)")
                        cargarDatos()
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        println("Error al actualizar producto: ${t.message}")
                    }
                })

                showDialogSacarProducto = false
                bodegaSeleccionadaParaSacar = null
            },
            onDismiss = {
                showDialogSacarProducto = false
                bodegaSeleccionadaParaSacar = null
            }
        )
    }

}