package com.example.medicare.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

class RegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as MedicareApp
        val viewModel = ViewModelProvider(
            this, 
            AuthViewModelFactory(app.usuarioRepository)
        )[AuthViewModel::class.java]

        setContent {
            MediCareTheme {
                val usuarioLogueado by viewModel.usuarioLogueado.observeAsState()

                LaunchedEffect(usuarioLogueado) {
                    if (usuarioLogueado != null) {
                        Toast.makeText(this@RegistroActivity, "Registro exitoso. Inicia sesión", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                        finish()
                    }
                }

                RegistroScreen(
                    viewModel = viewModel,
                    onIrALogin = { finish() }
                )
            }
        }
    }
}

@Composable
fun RegistroScreen(
    viewModel: AuthViewModel, 
    onIrALogin: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    val azulClaro = Color(0xFF66B2FF)
    
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val mensajeError by viewModel.mensajeError.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(azulClaro, azul))), 
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 70.dp), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "¡Bienvenido!",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White)
            Spacer(modifier = Modifier.height(15.dp))
            Text(text ="\"Tu salud es lo primero\nDéjanos recordártelo\"",
                fontSize = 19.sp,
                fontWeight = FontWeight.Light,
                color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).wrapContentHeight(), 
                    shape = RoundedCornerShape(20.dp), 
                    elevation = CardDefaults.cardElevation(12.dp), 
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Registrarse", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = azul)
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(text = "Nombre completo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = azulClaro, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(
                            value = nombre, 
                            onValueChange = { nombre = it }, 
                            placeholder = { Text("Juan Pérez") }, 
                            singleLine = true, 
                            modifier = Modifier.fillMaxWidth(), 
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Text(text = "Correo electrónico", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = azulClaro, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(
                            value = correo, 
                            onValueChange = { correo = it }, 
                            placeholder = { Text("@gmail.com") }, 
                            singleLine = true, 
                            modifier = Modifier.fillMaxWidth(), 
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Text(text = "Contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = azulClaro, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        OutlinedTextField(
                            value = contrasena, 
                            onValueChange = { contrasena = it }, 
                            placeholder = { Text("**********") }, 
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = { 
                                IconButton(onClick = { passwordVisible = !passwordVisible }) { 
                                    Icon(painter = painterResource(id = if (passwordVisible) R.drawable.invisible else R.drawable.ojo), contentDescription = null, modifier = Modifier.size(22.dp)) 
                                } 
                            },
                            modifier = Modifier.fillMaxWidth(), 
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (!mensajeError.isNullOrBlank()) { 
                            Text(text = mensajeError!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp)) 
                        }

                        Spacer(modifier = Modifier.height(25.dp))
                        
                        Button(
                            onClick = { viewModel.registrarUsuario(nombre, correo, contrasena) }, 
                            modifier = Modifier.fillMaxWidth().height(55.dp), 
                            shape = RoundedCornerShape(50.dp), 
                            colors = ButtonDefaults.buttonColors(containerColor = azul)
                        ) {
                            Text(text = "Registrarse", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        
                        Row {
                            Text("¿Ya tienes cuenta? ", fontWeight = FontWeight.Medium)
                            Text(
                                text = "Iniciar Sesión", 
                                color = Color(0XFF2E7D32), 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.clickable { onIrALogin() }
                            )
                        }
                    }
                }
            }
        }
    }
}
