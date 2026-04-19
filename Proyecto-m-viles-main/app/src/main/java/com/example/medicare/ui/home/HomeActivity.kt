package com.example.medicare.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare.MedicareApp
import com.example.medicare.R
import com.example.medicare.ui.enfermedades.EnfermedadesActivity
import com.example.medicare.ui.medicamentos.MedicamentosActivity
import com.example.medicare.ui.perfil.PerfilActivity
import com.example.medicare.ui.theme.MediCareTheme

class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as MedicareApp).historialRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nombre = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        val correo = intent.getStringExtra("CORREO_USUARIO") ?: ""

        setContent {
            MediCareTheme {
                LaunchedEffect(idUsuario) {
                    if (idUsuario != -1) viewModel.cargarDatosHome(idUsuario)
                }

                HomeScreen(
                    nombreUsuario = nombre,
                    idUsuario = idUsuario,
                    viewModel = viewModel,
                    onIrAEnfermedades = {
                        startActivity(Intent(this, EnfermedadesActivity::class.java).apply { putExtra("ID_USUARIO", idUsuario) })
                    },
                    onIrAMedicamentos = {
                        startActivity(Intent(this, MedicamentosActivity::class.java).apply { putExtra("ID_USUARIO", idUsuario) })
                    },
                    onIrAPerfil = {
                        startActivity(Intent(this, PerfilActivity::class.java).apply { 
                            putExtra("NOMBRE_USUARIO", nombre)
                            putExtra("CORREO_USUARIO", correo)
                        })
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        if (idUsuario != -1) {
            viewModel.cargarDatosHome(idUsuario)
        }
    }
}

@Composable
fun HomeScreen(nombreUsuario: String, idUsuario: Int, viewModel: HomeViewModel, onIrAEnfermedades: () -> Unit, onIrAMedicamentos: () -> Unit, onIrAPerfil: () -> Unit) {
    val azul = Color(0xFF0086FF)
    val proximasDosis by viewModel.proximasDoses.collectAsState()
    val historial by viewModel.historial.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF66B2FF), Color(0xFF0086FF))), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(vertical = 40.dp, horizontal = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "¡Hola, $nombreUsuario!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "MediAlert: Tu Salud, Primero.", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    IconButton(onClick = onIrAPerfil) { Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
                }
            }

            // Usamos una sola LazyColumn para todo el contenido scrollable
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                // SECCIÓN PRÓXIMAS DOSIS
                item {
                    Text(text = "Próximas dosis", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }

                if (proximasDosis.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
                            Text("No hay tomas pendientes por hoy", modifier = Modifier.padding(20.dp), color = Color.Gray)
                        }
                    }
                } else {
                    // AQUÍ ESTÁ EL CAMBIO: Muestra todas las dosis, no solo la primera
                    items(proximasDosis) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(), 
                            shape = RoundedCornerShape(16.dp), 
                            elevation = CardDefaults.cardElevation(4.dp), 
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(text = p.nombre_medicamento, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "A las ${p.hora_primera_toma}", color = Color.Gray)
                                    }
                                    Text(text = p.dosis, fontWeight = FontWeight.Bold, color = azul)
                                }
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.registrarTomaManual(p.idProgramacion, p.hora_primera_toma, "Tomado", idUsuario) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32)),
                                        contentPadding = PaddingValues(4.dp)
                                    ) { Text("TOMADO", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                                    Button(
                                        onClick = { viewModel.registrarTomaManual(p.idProgramacion, p.hora_primera_toma, "Pospuesto", idUsuario) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0), contentColor = Color(0xFFE65100)),
                                        contentPadding = PaddingValues(4.dp)
                                    ) { Text("POSPONER", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                                    Button(
                                        onClick = { viewModel.registrarTomaManual(p.idProgramacion, p.hora_primera_toma, "No Tomado", idUsuario) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFC62828)),
                                        contentPadding = PaddingValues(4.dp)
                                    ) { Text("OMITIR", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }

                // SECCIÓN HISTORIAL
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Historial reciente", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        TextButton(onClick = { viewModel.limpiarTodoElHistorial(idUsuario) }) {
                            Text("Limpiar todo", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }

                if (historial.isEmpty()) {
                    item {
                        Text("Registra tus tomas para ver el historial aquí", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    items(historial) { h ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                val iconColor = when(h.estado) {
                                    "Tomado" -> Color(0xFF4CAF50)
                                    "Pospuesto" -> Color(0xFFFF9800)
                                    else -> Color.Red
                                }
                                Icon(painterResource(if(h.estado == "Tomado") R.drawable.exitoso else R.drawable.no_exitoso), null, Modifier.size(24.dp), tint = iconColor)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(h.nombre_medicamento, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${h.estado} - ${h.fecha_hora_real}", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.eliminarRegistroHistorial(h.idToma, idUsuario) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Barra inferior (está fuera de la LazyColumn para que sea fija)
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(painterResource(R.drawable.home), null, Modifier.size(26.dp)) }, label = { Text("Inicio") })
                NavigationBarItem(selected = false, onClick = onIrAEnfermedades, icon = { Icon(painterResource(R.drawable.emfermedad), null, Modifier.size(26.dp)) }, label = { Text("Enfermedades") })
                NavigationBarItem(selected = false, onClick = onIrAMedicamentos, icon = { Icon(painterResource(R.drawable.medicamento), null, Modifier.size(26.dp)) }, label = { Text("Medicamentos") })
            }
        }

        // Botón flotante
        FloatingActionButton(onClick = { menuExpandido = !menuExpandido }, containerColor = azul, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 100.dp), shape = CircleShape) {
            Icon(painterResource(id = R.drawable.agregar), null, tint = Color.White)
        }

        if (menuExpandido) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)).clickable { menuExpandido = false })
            Card(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 100.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.width(200.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().clickable { onIrAEnfermedades(); menuExpandido = false }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(id = R.drawable.emfermedad), null, Modifier.size(20.dp), tint = azul)
                        Spacer(Modifier.width(12.dp)); Text("Enfermedad")
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { onIrAMedicamentos(); menuExpandido = false }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(id = R.drawable.medicamento), null, Modifier.size(20.dp), tint = azul)
                        Spacer(Modifier.width(12.dp)); Text("Medicamento")
                    }
                }
            }
        }
    }
}
