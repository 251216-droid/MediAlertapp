package com.example.medicare.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.MedicareApp
import com.example.medicare.R
import com.example.medicare.ui.theme.MediCareTheme
import com.example.medicare.ui.home.HomeActivity

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as MedicareApp
        val viewModel = ViewModelProvider(this, AuthViewModelFactory(app.usuarioRepository))[AuthViewModel::class.java]

        setContent {
            MediCareTheme {
                val usuarioLogueado by viewModel.usuarioLogueado.observeAsState()

                LaunchedEffect(usuarioLogueado) {
                    usuarioLogueado?.let { user ->
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            putExtra("NOMBRE_USUARIO", user.nombre)
                            putExtra("ID_USUARIO", user.idUsuario)
                            putExtra("CORREO_USUARIO", user.correo)
                        }
                        startActivity(intent)
                        finish()
                    }
                }

                LoginScreen(viewModel = viewModel, onIrARegistro = { startActivity(Intent(this, RegistroActivity::class.java)) })
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: AuthViewModel, onIrARegistro: () -> Unit) {
    val azul = Color(0xFF0086FF)
    val azulClaro = Color(0xFF66B2FF)
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val mensajeError by viewModel.mensajeError.observeAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(azulClaro, azul))), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 70.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "¡Bienvenido!", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "\"Tu salud es lo primero\"", fontSize = 19.sp, color = Color.White)
            Spacer(modifier = Modifier.height(30.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(550.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Iniciar Sesión", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = azul)
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(painterResource(if (passwordVisible) R.drawable.invisible else R.drawable.ojo), null, modifier = Modifier.size(22.dp)) } },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    if (!mensajeError.isNullOrBlank()) { Text(text = mensajeError!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp)) }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(onClick = { viewModel.iniciarSesion(correo, contrasena) }, modifier = Modifier.fillMaxWidth().height(55.dp), shape = RoundedCornerShape(50.dp), colors = ButtonDefaults.buttonColors(containerColor = azul)) {
                        Text(text = "Iniciar Sesión", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Text(text = "¿No tienes cuenta? ")
                        Text(text = "Registrarse", color = Color(0XFF2E7D32), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onIrARegistro() })
                    }
                }
            }
        }
    }
}
