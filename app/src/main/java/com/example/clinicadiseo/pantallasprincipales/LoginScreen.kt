
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clinicadiseo.R

@Composable
fun LoginScreen(navController: NavController) {
    val poppins = FontFamily(Font(R.font.poppins))
    val poppinsnormal = FontFamily(Font(R.font.poppinsnormal))
    val poppinsbold = FontFamily(Font(R.font.poppinsbold))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff2E8B57)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoalt),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Bienvenido al Sistema",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff1A5D1A),
                    fontFamily = poppinsbold,
                    fontSize = 21.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario", fontWeight = FontWeight.Bold, fontFamily = poppinsnormal) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", fontWeight = FontWeight.Bold, fontFamily = poppinsnormal) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        try {
                            navController.navigate("home")
                        } catch (e: Exception) {
                            Log.e("NavigationError", "Error al navegar a Home: ${e.message}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff1A5D1A),
                        contentColor = Color.White,
                    )
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontFamily = poppinsbold, fontSize = 18.sp)
                }
            }
        }
    }
}

