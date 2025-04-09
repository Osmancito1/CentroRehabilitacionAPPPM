package com.example.clinicadiseo.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.clinicadiseo.Components.CategoriesSection
import com.example.clinicadiseo.Components.DrawerContent
import com.example.clinicadiseo.R
import com.example.clinicadiseo.pantallas.*
import com.example.clinicadiseo.utils.decodeJwt
import com.example.clinicadiseo.utils.withAdminAccess
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Pacientes : Screen("pacientes")
    object Encargados : Screen("encargados")
    object Terapeutas : Screen("terapeutas")
    object Diagnosticos : Screen("diagnosticos")
    object Citas : Screen("citas")
    object Productos : Screen("productos")
    object Compras : Screen("compras")
    object Prestamos : Screen("prestamos")
    object Bodegas : Screen("bodegas")
    object Usuarios : Screen("usuarios")
    object ComprasForm : Screen("compras_form")
}

val poppinsbold = FontFamily(Font(R.font.poppinsbold))
val poppins = FontFamily(Font(R.font.poppins))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(externalNavController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val internalNavController = rememberNavController()
    val currentBackStackEntry = internalNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val context = LocalContext.current
    var userName by remember { mutableStateOf("Usuario") }
    var rolUsuario by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (!token.isNullOrEmpty()) {
            val payload = decodeJwt(token)
            userName = payload["nombre"] ?: "Usuario"
            rolUsuario = payload["rol"] ?: ""
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (drawerState.isOpen) 180f else 0f,
        animationSpec = tween(300)
    )

    val opacity = remember { Animatable(0f) }
    LaunchedEffect(Unit) { opacity.animateTo(1f, animationSpec = tween(500)) }

    val scale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) { scale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing)) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            AnimatedVisibility(
                visible = drawerState.isOpen,
                enter = fadeIn(tween(300)) + slideInHorizontally(),
                exit = fadeOut(tween(200)) + slideOutHorizontally()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight()
                        .background(Color(0xff2E8B57))
                        .padding(16.dp)
                ) {
                    DrawerContent(
                        appNavController = externalNavController,
                        sectionNavController = internalNavController,
                        rolUsuario = rolUsuario
                    ) {
                        scope.launch { drawerState.close() }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AnimatedVisibility(
                                visible = !drawerState.isOpen,
                                enter = fadeIn(tween(500)),
                                exit = fadeOut(tween(200))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logoalt),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            }
                            AnimatedVisibility(
                                visible = !drawerState.isOpen,
                                enter = fadeIn(tween(500)),
                                exit = fadeOut(tween(200))
                            ) {
                                Text(
                                    "Centro de Rehabilitación",
                                    fontFamily = poppinsbold,
                                    fontSize = 22.sp,
                                    modifier = Modifier.alpha(opacity.value),
                                    color = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xff2E8B57),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer { rotationZ = rotation }
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = currentRoute == "categorias",
                    enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.9f),
                    exit = fadeOut(tween(500)) + scaleOut(targetScale = 1.1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color(0xff2E8B57))
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Rol: $rolUsuario",
                                color = Color.White,
                                fontFamily = poppinsbold,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .alpha(opacity.value)
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut(tween(300)) + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    NavHost(navController = internalNavController, startDestination = "categorias") {
                        composable("categorias") {
                            CategoriesSection(internalNavController, screenHeight, rolUsuario)
                        }
                        composable(Screen.Pacientes.route) { PacientesScreen() }
                        composable(Screen.Encargados.route) { EncargadosScreen() }
                        composable(Screen.Terapeutas.route) { TerapeutasScreen() }
                        composable(Screen.Diagnosticos.route) { DiagnosticosScreen() }
                        composable(Screen.Citas.route) { CitasScreen() }

                        composable(Screen.Productos.route) {
                            withAdminAccess(rolUsuario) { ProductosScreen() }
                        }
                        composable(Screen.Compras.route) {
                            withAdminAccess(rolUsuario) { ComprasScreen(navController = internalNavController) }
                        }
                        composable(Screen.Prestamos.route) {
                            withAdminAccess(rolUsuario) { PrestamosScreen() }
                        }
                        composable(Screen.Bodegas.route) {
                            withAdminAccess(rolUsuario) { BodegasScreen() }
                        }
                        composable(Screen.Usuarios.route) {
                            withAdminAccess(rolUsuario) { UsuariosScreen() }
                        }
                        composable("compras_form/{id}") { backStackEntry ->
                            withAdminAccess(rolUsuario) {
                                ComprasFormScreen(
                                    navController = internalNavController,
                                    backStackEntry = backStackEntry
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

