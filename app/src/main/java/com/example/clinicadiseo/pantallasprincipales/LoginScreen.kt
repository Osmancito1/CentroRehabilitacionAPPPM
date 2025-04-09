package com.example.clinicadiseo.pantallasprincipales

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clinicadiseo.R
import com.example.clinicadiseo.Data_Models.LoginRequest
import com.example.clinicadiseo.Data_Models.LoginResponse
import com.example.clinicadiseo.Api_Services.RetrofitClient
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
fun LoginScreen(navController: NavController) {
    val poppinsnormal = FontFamily(Font(R.font.poppinsnormal))
    val poppinsbold = FontFamily(Font(R.font.poppinsbold))

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSnackbarVisible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isSnackbarVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fotologinapp),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x992E8B57),
                            Color(0xCC1A5D1A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoalt),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val configuration = LocalConfiguration.current
                    val screenWidth = configuration.screenWidthDp

                    val dynamicFontSize = when {
                        screenWidth < 360 -> 16.sp
                        screenWidth < 400 -> 18.sp
                        else -> 22.sp
                    }

                    Text(
                        "Bienvenido al Sistema",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff1A5D1A),
                        fontFamily = poppinsbold,
                        fontSize = dynamicFontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario", fontFamily = poppinsnormal) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Usuario")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", fontFamily = poppinsnormal) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Mostrar u ocultar contraseña"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xff1A5D1A),
                            focusedLabelColor = Color(0xff1A5D1A),
                            cursorColor = Color(0xff1A5D1A)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Por favor completa todos los campos")
                                }
                                return@Button
                            }

                            val loginData = LoginRequest(email = username, password = password)

                            RetrofitClient.instance.login(loginData).enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(
                                    call: Call<LoginResponse>,
                                    response: Response<LoginResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val token = response.body()?.token ?: ""
                                        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                        prefs.edit().putString("jwt_token", token).apply()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("¡Bienvenido al sistema!")
                                            navController.navigate("home")
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Usuario o contraseña incorrectos")
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error de conexión")
                                    }
                                }
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff1A5D1A),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontFamily = poppinsbold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            snackbar = { snackbarData ->
                isSnackbarVisible = true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A5D1A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .scale(scale),
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
        )
    }
}

