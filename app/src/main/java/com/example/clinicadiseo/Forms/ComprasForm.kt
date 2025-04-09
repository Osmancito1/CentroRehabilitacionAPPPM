package com.example.clinicadiseo.pantallas

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasFormScreen(navController: NavHostController, backStackEntry: NavBackStackEntry){
    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fecha by remember { mutableStateOf("") }
    var donante by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    val detalleCompra = remember { mutableStateListOf<DetalleCompra>() }
    var showProductoMenu by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var costoUnitario by remember { mutableStateOf("") }
    var indexEditando by remember { mutableStateOf(-1) }

    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            fecha = formatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getProductos().enqueue(object : Callback<ProductoResponse> {
            override fun onResponse(call: Call<ProductoResponse>, response: Response<ProductoResponse>) {
                if (response.isSuccessful) {
                    productos = response.body()?.result ?: emptyList()
                }
            }

            override fun onFailure(call: Call<ProductoResponse>, t: Throwable) {
                Log.e("Compra", "Error al obtener productos: ${t.message}")
            }
        })

        if (id != 0) {
            RetrofitClient.instance.getCompras().enqueue(object : Callback<CompraResponse> {
                override fun onResponse(call: Call<CompraResponse>, response: Response<CompraResponse>) {
                    if (response.isSuccessful) {
                        val compra = response.body()?.result?.find { it.id_compra == id }
                        compra?.let {
                            fecha = it.fecha
                            donante = it.donante
                            detalleCompra.clear()
                            detalleCompra.addAll(it.detalle)
                        }
                    }
                }

                override fun onFailure(call: Call<CompraResponse>, t: Throwable) {
                    Log.e("Compra", "Error al obtener compra para editar: ${t.message}")
                }
            })
        }
    }
    fun calcularTotal(): Double {
        return detalleCompra.sumOf { it.cantidad * it.costo_unitario }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Registrar Compra", fontFamily = poppinsbold, fontSize = 22.sp, color = Color(0xFF1A5D1A))
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = donante,
                    onValueChange = { donante = it },
                    label = { Text("Donante", fontFamily = poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A), cursorColor = Color(0xFF1A5D1A))
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha", fontFamily = poppins) },
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A), cursorColor = Color(0xFF1A5D1A))
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Agregar Producto", fontFamily = poppinsbold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = showProductoMenu,
                    onExpandedChange = { showProductoMenu = it }
                ) {
                    OutlinedTextField(
                        value = productoSeleccionado?.nombre ?: "Seleccionar producto",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Producto") },
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A), cursorColor = Color(0xFF1A5D1A))
                    )
                    ExposedDropdownMenu(
                        expanded = showProductoMenu,
                        onDismissRequest = { showProductoMenu = false }
                    ) {
                        productos.forEach {
                            DropdownMenuItem(text = { Text(it.nombre) }, onClick = {
                                productoSeleccionado = it
                                showProductoMenu = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) cantidad = it
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A), cursorColor = Color(0xFF1A5D1A))
                )

                OutlinedTextField(
                    value = costoUnitario,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) costoUnitario = it
                    },
                    label = { Text("Costo Unitario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A5D1A), cursorColor = Color(0xFF1A5D1A))
                )

                Button(
                    onClick = {
                        val cantidadInt = cantidad.toIntOrNull() ?: 0
                        val costoDouble = costoUnitario.toDoubleOrNull() ?: 0.0
                        if (productoSeleccionado != null && cantidadInt > 0 && costoDouble > 0.0) {
                            if (indexEditando >= 0) {
                                detalleCompra[indexEditando] = DetalleCompra(0, productoSeleccionado!!.id_producto, cantidadInt, costoDouble)
                                indexEditando = -1
                            } else {
                                detalleCompra.add(DetalleCompra(0, productoSeleccionado!!.id_producto, cantidadInt, costoDouble))
                            }
                            productoSeleccionado = null
                            cantidad = ""
                            costoUnitario = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
                ) {
                    Text(if (indexEditando >= 0) "Actualizar Producto" else "Agregar a lista", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Productos Agregados", fontFamily = poppinsbold, fontSize = 18.sp)

                if (detalleCompra.isEmpty()) {
                    Text("No hay productos agregados aún.", fontFamily = poppins, color = Color.Gray)
                } else {
                    Column(modifier = Modifier.fillMaxHeight(0.6f)) {
                        detalleCompra.forEachIndexed { index, item ->
                            val nombreProducto = productos.find { it.id_producto == item.id_producto }?.nombre ?: ""
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("$nombreProducto x${item.cantidad}", fontFamily = poppinsbold)
                                    Text("Costo Unitario: L. ${item.costo_unitario}", fontFamily = poppins)
                                    Text("Subtotal: L. ${"%.2f".format(item.cantidad * item.costo_unitario)}", fontFamily = poppins)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        IconButton(onClick = {
                                            val producto = productos.find { it.id_producto == item.id_producto }
                                            productoSeleccionado = producto
                                            cantidad = item.cantidad.toString()
                                            costoUnitario = item.costo_unitario.toString()
                                            indexEditando = index
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1A5D1A))
                                        }
                                        IconButton(onClick = {
                                            detalleCompra.removeAt(index)
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Total: L. ${"%.2f".format(calcularTotal())}", fontFamily = poppinsbold)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (fecha.isBlank() || donante.isBlank() || detalleCompra.isEmpty()) {
                            scope.launch { snackbarHostState.showSnackbar("Completa todos los campos") }
                            return@Button
                        }

                        val nuevaCompra = CompraRequest(
                            fecha = fecha,
                            donante = donante,
                            total = calcularTotal(),
                            detalle = detalleCompra.toList()
                        )

                        val call = if (id != 0) {
                            RetrofitClient.instance.updateCompra(id, nuevaCompra)
                        } else {
                            RetrofitClient.instance.insertCompra(nuevaCompra)
                        }

                        call.enqueue(object : Callback<ApiResponse> {
                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                detalleCompra.forEach { detalle ->
                                    val producto = productos.find { it.id_producto == detalle.id_producto }
                                    producto?.let {
                                        val actualizado = it.copy(cantidad_disponible = it.cantidad_disponible + detalle.cantidad)
                                        RetrofitClient.instance.updateProducto(it.id_producto, actualizado).enqueue(object : Callback<ApiResponse> {
                                            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {}
                                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
                                        })
                                    }
                                }

                                scope.launch {
                                    snackbarHostState.showSnackbar("Compra ${if (id != 0) "actualizada" else "guardada"} exitosamente")
                                    navController.popBackStack()
                                }
                            }

                            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                scope.launch { snackbarHostState.showSnackbar("Error al guardar compra") }
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A5D1A))
                ) {
                    Text("Guardar Compra", color = Color.White)
                }
            }
        }
    }
}
