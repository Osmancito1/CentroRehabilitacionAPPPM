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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
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
import androidx.navigation.NavController
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import com.example.clinicadiseo.Reports.generarYCompartirPDFCompra
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
fun ComprasScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var compras by remember { mutableStateOf<List<Compra>>(emptyList()) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var compraSeleccionada by remember { mutableStateOf<Compra?>(null) }

    fun cargarDatos() {
        loading = true
        RetrofitClient.instance.getCompras().enqueue(object : Callback<CompraResponse> {
            override fun onResponse(call: Call<CompraResponse>, response: Response<CompraResponse>) {
                if (response.isSuccessful) {
                    compras = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<CompraResponse>, t: Throwable) {
                Log.e("ComprasScreen", "Error al obtener compras: ${t.message}")
                loading = false
            }
        })

        RetrofitClient.instance.getProductos().enqueue(object : Callback<ProductoResponse> {
            override fun onResponse(call: Call<ProductoResponse>, response: Response<ProductoResponse>) {
                if (response.isSuccessful) {
                    productos = response.body()?.result ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ProductoResponse>, t: Throwable) {
                Log.e("ComprasScreen", "Error al obtener productos: ${t.message}")
            }
        })
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val comprasFiltradas = compras.filter {
        it.donante.contains(search, ignoreCase = true)
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
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Compras",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A)
                    )
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("compras_form/0")
                        },
                        containerColor = Color(0xFF1A5D1A),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar compra")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar por donante", fontFamily = poppins) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1A5D1A),
                        cursorColor = Color(0xFF1A5D1A)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        items(comprasFiltradas) { compra ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        fontFamily = poppinsbold,
                                        text = "${compra.donante}",
                                        fontSize = 18.sp
                                    )
                                    Text("Fecha: ${compra.fecha}", fontFamily = poppins)
                                    Text("Total: L. ${"%.2f".format(compra.total)}", fontFamily = poppins)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            compraSeleccionada = compra
                                        }) {
                                            Icon(Icons.Default.Visibility, contentDescription = "Ver", tint = Color(0xFF1565C0))
                                        }
                                        IconButton(onClick = {
                                            navController.navigate("compras_form/${compra.id_compra}")
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }
                                        IconButton(onClick = {
                                            RetrofitClient.instance.deleteCompra(compra.id_compra).enqueue(object : Callback<ApiResponse> {
                                                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                                    cargarDatos()
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        snackbarHostState.showSnackbar("Compra eliminada exitosamente")
                                                    }
                                                }

                                                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error al eliminar compra")
                                                    }
                                                }
                                            })
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                        IconButton(onClick = {
                                            generarYCompartirPDFCompra(context, compra)
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

    compraSeleccionada?.let { compra ->
        AlertDialog(
            onDismissRequest = { compraSeleccionada = null },
            title = {
                Text(
                    "Detalle de la Compra",
                    fontFamily = poppinsbold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A5D1A)
                )
            },
            text = {
                Column {
                    Row {
                        Text("Donante: ", fontFamily = poppinsbold, color = Color(0xFF1A5D1A))
                        Text(compra.donante, fontFamily = poppins, color = Color.Black)
                    }
                    Row {
                        Text("Fecha: ", fontFamily = poppinsbold, color = Color(0xFF1A5D1A))
                        Text(compra.fecha, fontFamily = poppins, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Productos:", fontFamily = poppinsbold, color = Color(0xFF1A5D1A))
                    compra.detalle.forEach {
                        val nombre = productos.find { p -> p.id_producto == it.id_producto }?.nombre ?: "Producto"
                        Text(
                            "- $nombre: ${it.cantidad} x L. ${it.costo_unitario}",
                            fontFamily = poppinsbold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text("Total: ", fontFamily = poppinsbold, color = Color(0xFF1A5D1A))
                        Text("L. ${"%.2f".format(compra.total)}", fontFamily = poppinsbold, color = Color.Black)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { compraSeleccionada = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cerrar", fontFamily = poppinsbold)
                }
            },
            containerColor = Color.White
        )


    }
}