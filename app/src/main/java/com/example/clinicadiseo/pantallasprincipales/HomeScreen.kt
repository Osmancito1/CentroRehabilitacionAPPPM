import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.clinicadiseo.pantallas.BodegasScreen
import com.example.clinicadiseo.pantallas.CitasScreen
import com.example.clinicadiseo.pantallas.ComprasScreen
import com.example.clinicadiseo.pantallas.DiagnosticoScreen
import com.example.clinicadiseo.pantallas.EncargadosScreen
import com.example.clinicadiseo.pantallas.PacientesScreen
import com.example.clinicadiseo.pantallas.PrestamosScreen
import com.example.clinicadiseo.pantallas.ProductosScreen
import com.example.clinicadiseo.pantallas.TerapeutasScreen
import com.example.clinicadiseo.pantallas.UsuariosScreen
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
}


val poppinsnormal = FontFamily(Font(R.font.poppinsnormal))
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight()
                        .background(Color(0xff2E8B57))
                        .padding(16.dp)
                ) {
                    DrawerContent(
                        appNavController = externalNavController,
                        sectionNavController = internalNavController
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
                        Text("Centro de Rehabilitación", fontFamily = poppinsbold, fontSize = 22.sp)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xff2E8B57),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                if (currentRoute == "categorias") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xff2E8B57)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Usuario Registrado: Osman",
                            color = Color.White,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = poppins,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(navController = internalNavController, startDestination = "categorias") {
                    composable("categorias") {
                        CategoriesSection(internalNavController)
                    }
                    composable(Screen.Pacientes.route) {
                        PacientesScreen()
                    }
                    composable(Screen.Encargados.route) {
                        EncargadosScreen()
                    }
                    composable(Screen.Terapeutas.route) {
                        TerapeutasScreen()
                    }
                    composable(Screen.Diagnosticos.route) {
                        DiagnosticoScreen()
                    }
                    composable(Screen.Citas.route) {
                        CitasScreen()
                    }
                    composable(Screen.Productos.route) {
                        ProductosScreen()
                    }
                    composable(Screen.Compras.route) {
                        ComprasScreen()
                    }
                    composable(Screen.Prestamos.route) {
                        PrestamosScreen()
                    }
                    composable(Screen.Bodegas.route) {
                        BodegasScreen()
                    }
                    composable(Screen.Usuarios.route) {
                        UsuariosScreen()
                    }
                }
            }

        }
    }
}


