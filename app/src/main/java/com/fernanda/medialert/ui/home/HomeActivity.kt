package com.fernanda.medialert.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.core.content.ContextCompat
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.R
import com.fernanda.medialert.ui.enfermedades.EnfermedadesActivity
import com.fernanda.medialert.ui.medicamentos.MedicamentosActivity
import com.fernanda.medialert.ui.perfil.PerfilActivity
import com.fernanda.medialert.ui.theme.MediAlertTheme

class HomeActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as MediAlertApp).historialRepository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permiso otorgado o negado */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val nombre    = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        val correo    = intent.getStringExtra("CORREO_USUARIO") ?: ""

        setContent {
            MediAlertTheme {
                LaunchedEffect(idUsuario) {
                    if (idUsuario != -1) viewModel.cargarDatosHome(idUsuario)
                }
                HomeScreen(
                    nombreUsuario     = nombre,
                    idUsuario         = idUsuario,
                    viewModel         = viewModel,
                    onIrAEnfermedades = {
                        startActivity(Intent(this, EnfermedadesActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuario)
                            putExtra("NOMBRE_USUARIO", nombre)
                            putExtra("CORREO_USUARIO", correo)
                        })
                    },
                    onIrAMedicamentos = {
                        startActivity(Intent(this, MedicamentosActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuario)
                            putExtra("NOMBRE_USUARIO", nombre)
                            putExtra("CORREO_USUARIO", correo)
                        })
                    },
                    onIrAPerfil = {
                        startActivity(Intent(this, PerfilActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuario)
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
        if (idUsuario != -1) viewModel.cargarDatosHome(idUsuario)
    }
}

@Composable
fun HomeScreen(
    nombreUsuario: String,
    idUsuario: Int,
    viewModel: HomeViewModel,
    onIrAEnfermedades: () -> Unit,
    onIrAMedicamentos: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val azul          = Color(0xFF0086FF)
    val proximasDosis by viewModel.proximasDoses.collectAsState()
    val historial     by viewModel.historial.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    var menuExpandido by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER AZUL FIJO ─────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF66B2FF), Color(0xFF0086FF))),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(top = 40.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "¡Hola, $nombreUsuario!",
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Text(
                            "MediAlert · Tu Salud, Primero.",
                            fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(onClick = onIrAPerfil) {
                        Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
            }

            // ── CONTENIDO SCROLLABLE ──────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 110.dp)
            ) {

                // ── PRÓXIMAS DOSIS ────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Próximas tomas",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)
                        )
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = azul,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                if (!isLoading && proximasDosis.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("", fontSize = 28.sp)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(
                                        "¡Todo al día!",
                                        fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        "No tienes tomas pendientes por ahora.",
                                        fontSize = 13.sp, color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                items(proximasDosis, key = { it.idProgramacion }) { p ->
                    val horaDisplay = p.proxima_toma.ifBlank { p.hora_primera_toma }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Info del medicamento
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        p.nombre_medicamento,
                                        fontSize = 17.sp, fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A2E)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(" ", fontSize = 14.sp)
                                        Text(
                                            horaDisplay,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = azul
                                        )
                                    }
                                    Text(
                                        "Cada ${p.frecuencia_horas}h · ${p.tipo_presentacion}",
                                        fontSize = 12.sp, color = Color.Gray
                                    )
                                }
                                // Dosis
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE3F2FD)
                                ) {
                                    Text(
                                        p.dosis,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = azul,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))
                            Divider(color = Color(0xFFF0F0F0))
                            Spacer(Modifier.height(12.dp))

                            // Botones — solo TOMADO y OMITIR en la app
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // ✅ TOMADO
                                Button(
                                    onClick = {
                                        viewModel.registrarTomaManual(
                                            p.idProgramacion, horaDisplay, "Tomado", idUsuario
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE8F5E9),
                                        contentColor   = Color(0xFF2E7D32)
                                    )
                                ) {
                                    Text(" TOMADO", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                // ❌ OMITIR
                                Button(
                                    onClick = {
                                        viewModel.registrarTomaManual(
                                            p.idProgramacion, horaDisplay, "No Tomado", idUsuario
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFEBEE),
                                        contentColor   = Color(0xFFC62828)
                                    )
                                ) {
                                    Text("OMITIR", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ── HISTORIAL RECIENTE ────────────────────────────
                item { Spacer(Modifier.height(4.dp)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Historial reciente",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)
                        )
                        TextButton(onClick = { viewModel.limpiarTodoElHistorial(idUsuario) }) {
                            Text("Limpiar todo", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }

                if (!isLoading && historial.isEmpty()) {
                    item {
                        Text(
                            "Aquí aparecerán tus tomas registradas.",
                            color = Color.Gray, fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                items(historial, key = { it.idToma }) { h ->
                    val (emoji, colorEstado) = when (h.estado) {
                        "Tomado"    -> "" to Color(0xFF4CAF50)
                        "Pospuesto" -> "" to Color(0xFFFF9800)
                        else        -> "" to Color(0xFFF44336)
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 22.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    h.nombre_medicamento,
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                                )
                                Text(
                                    "${h.estado} · ${h.fecha_hora_real}",
                                    fontSize = 11.sp, color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.eliminarRegistroHistorial(h.idToma, idUsuario)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete, null,
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── BARRA INFERIOR FIJA ───────────────────────────────
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Icon(painterResource(R.drawable.home), null, Modifier.size(24.dp)) },
                    label = { Text("Inicio", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false, onClick = onIrAEnfermedades,
                    icon = { Icon(painterResource(R.drawable.emfermedad), null, Modifier.size(24.dp)) },
                    label = { Text("Enfermedades", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false, onClick = onIrAMedicamentos,
                    icon = { Icon(painterResource(R.drawable.medicamento), null, Modifier.size(24.dp)) },
                    label = { Text("Medicamentos", fontSize = 11.sp) }
                )
            }
        }

        // ── BOTÓN FLOTANTE ────────────────────────────────────────
        FloatingActionButton(
            onClick = { menuExpandido = !menuExpandido },
            containerColor = azul,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp),
            shape = CircleShape
        ) {
            Icon(painterResource(R.drawable.agregar), null, tint = Color.White)
        }

        if (menuExpandido) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { menuExpandido = false }
            )
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.width(200.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onIrAEnfermedades(); menuExpandido = false }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.emfermedad), null, Modifier.size(20.dp), tint = azul)
                        Spacer(Modifier.width(12.dp))
                        Text("Nueva Enfermedad")
                    }
                    Divider(color = Color(0xFFF0F0F0))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onIrAMedicamentos(); menuExpandido = false }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.medicamento), null, Modifier.size(20.dp), tint = azul)
                        Spacer(Modifier.width(12.dp))
                        Text("Nuevo Medicamento")
                    }
                }
            }
        }
    }
}


