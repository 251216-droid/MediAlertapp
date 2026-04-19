package com.fernanda.medialert.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.ui.auth.LoginActivity
import com.fernanda.medialert.ui.theme.MediAlertTheme
import kotlinx.coroutines.launch

class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        val nombre = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val correo = intent.getStringExtra("CORREO_USUARIO") ?: "Sin correo"
        val repository = (application as MediAlertApp).usuarioRepository

        setContent {
            MediAlertTheme {
                PerfilScreen(
                    idUsuario = idUsuario,
                    nombreInicial = nombre,
                    correoInicial = correo,
                    repository = repository,
                    onVolver = { finish() },
                    onCerrarSesion = {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PerfilScreen(
    idUsuario: Int,
    nombreInicial: String,
    correoInicial: String,
    repository: com.fernanda.medialert.data.repositories.UsuarioRepository,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(nombreInicial) }
    var correo by remember { mutableStateOf(correoInicial) }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    // Header fijo + contenido scrollable
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

        // HEADER AZUL - FIJO (no scrollea)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(azulClaro, azulFuerte)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text("Perfil", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = azulFuerte, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = correo, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
        }

        // CONTENIDO SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón Editar / Cancelar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mi información", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                TextButton(onClick = { editando = !editando; if (!editando) { nombre = nombreInicial; correo = correoInicial; nuevaPassword = ""; confirmarPassword = "" } }) {
                    Text(if (editando) "Cancelar" else " Editar", color = azulFuerte)
                }
            }

            if (!editando) {
                // MODO VISTA
                InfoCard(icon = Icons.Default.Person, label = "Nombre completo", value = nombre)
                InfoCard(icon = Icons.Default.Email, label = "Correo electrónico", value = correo)
                InfoCard(icon = Icons.Default.Lock, label = "Contraseña", value = "••••••••")
            } else {
                // MODO EDICIÓN
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Editar datos", fontWeight = FontWeight.Bold, color = azulFuerte)

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = azulFuerte) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = { correo = it },
                            label = { Text("Correo electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = azulFuerte) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Divider(color = Color(0xFFF0F0F0))
                        Text("Cambiar contraseña (opcional)", fontSize = 13.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = nuevaPassword,
                            onValueChange = { nuevaPassword = it },
                            label = { Text("Nueva contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = azulFuerte) },
                            trailingIcon = {
                                IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                                    Icon(if (mostrarPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                }
                            },
                            visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = confirmarPassword,
                            onValueChange = { confirmarPassword = it },
                            label = { Text("Confirmar contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = azulFuerte) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = confirmarPassword.isNotEmpty() && nuevaPassword != confirmarPassword
                        )
                        if (confirmarPassword.isNotEmpty() && nuevaPassword != confirmarPassword) {
                            Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (nombre.isBlank() || correo.isBlank()) {
                                    Toast.makeText(context, "El nombre y correo no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (nuevaPassword.isNotEmpty() && nuevaPassword != confirmarPassword) {
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                guardando = true
                                scope.launch {
                                    val exito = repository.actualizarPerfil(idUsuario, nombre, correo, nuevaPassword)
                                    guardando = false
                                    if (exito) {
                                        editando = false
                                        nuevaPassword = ""
                                        confirmarPassword = ""
                                        Toast.makeText(context, " Perfil actualizado", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, " Error al actualizar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !guardando,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulFuerte)
                        ) {
                            if (guardando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            else Text("Guardar cambios", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Acerca de MediAlert", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Qué es MediAlert?", fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                    Text(
                        text = "MediAlert es una aplicación diseñada para ayudarte a gestionar tus medicamentos y enfermedades de manera organizada.",
                        fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red)
            ) {
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0086FF), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}


