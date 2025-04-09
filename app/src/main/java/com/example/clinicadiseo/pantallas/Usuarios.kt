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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicadiseo.Api_Services.RetrofitClient
import com.example.clinicadiseo.Data_Models.*
import com.example.clinicadiseo.Forms.CrearUsuarioDialog
import com.example.clinicadiseo.screens.poppins
import com.example.clinicadiseo.screens.poppinsbold
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
fun UsuariosScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    var search by remember { mutableStateOf("") }
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<Usuario?>(null) }

    fun cargarUsuarios() {
        loading = true
        RetrofitClient.instance.getUsuarios().enqueue(object : Callback<UsuariosResponse> {
            override fun onResponse(call: Call<UsuariosResponse>, response: Response<UsuariosResponse>) {
                if (response.isSuccessful) {
                    usuarios = response.body()?.result ?: emptyList()
                }
                loading = false
            }

            override fun onFailure(call: Call<UsuariosResponse>, t: Throwable) {
                Log.e("UsuariosScreen", "Error: ${t.message}")
                loading = false
            }
        })
    }

    LaunchedEffect(Unit) { cargarUsuarios() }

    val usuariosFiltrados = usuarios.filter {
        it.nombre.contains(search, ignoreCase = true) || it.email.contains(search, ignoreCase = true)
    }

    if (showDialog) {
        CrearUsuarioDialog(
            usuarioExistente = usuarioAEditar,
            onDismiss = {
                showDialog = false
                usuarioAEditar = null
            },
            onCrear = { nuevoUsuario ->
                val call = if (usuarioAEditar != null) {
                    RetrofitClient.instance.updateUsuario(usuarioAEditar!!.id_usuario, nuevoUsuario)
                } else {
                    RetrofitClient.instance.insertUsuario(nuevoUsuario)
                }

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            showDialog = false
                            usuarioAEditar = null
                            cargarUsuarios()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("GuardarUsuario", "Fallo: ${t.message}")
                    }
                })
            },
            snackbarHostState = snackbarHostState,
            mostrarPasswordPorDefecto = usuarioAEditar != null
        )
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Usuarios",
                        fontSize = 26.sp,
                        fontFamily = poppinsbold,
                        color = Color(0xFF1A5D1A),
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            usuarioAEditar = null
                            showDialog = true
                        },
                        containerColor = Color(0xFF1A5D1A),
                        contentColor = Color.White,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar usuario por nombre o email", fontFamily = poppins) },
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
                        items(usuariosFiltrados) { usuario ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = usuario.nombre,
                                        fontSize = 20.sp,
                                        fontFamily = poppinsbold,
                                        color = Color(0xFF1A5D1A)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row {
                                        Text("Email: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(usuario.email, fontFamily = poppins, color = Color.Black)
                                    }

                                    Row {
                                        Text("Rol: ", fontFamily = poppinsbold, color = Color.Black)
                                        Text(usuario.rol, fontFamily = poppins, color = Color.Black)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    val estadoColor = if (usuario.estado == "Activo") Color(0xFF4CAF50) else Color(0xFFD32F2F)
                                    Text(
                                        text = usuario.estado,
                                        fontSize = 12.sp,
                                        fontFamily = poppinsbold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .background(estadoColor, shape = RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            usuarioAEditar = usuario
                                            showDialog = true
                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar("Usuario guardado exitosamente")
                                            }
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2E7D32))
                                        }

                                        IconButton(onClick = {
                                            RetrofitClient.instance.deleteUsuario(usuario.id_usuario)
                                                .enqueue(object : Callback<Void> {
                                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                        if (response.isSuccessful) {
                                                            cargarUsuarios()
                                                            CoroutineScope(Dispatchers.Main).launch {
                                                                snackbarHostState.showSnackbar("Usuario eliminado exitosamente")
                                                            }
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                                        Log.e("EliminarUsuario", "Fallo: ${t.message}")
                                                    }
                                                })
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
}
