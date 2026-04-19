package com.fernanda.medialert.ui.historial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.R
import com.fernanda.medialert.data.remote.HistorialResponse
import com.fernanda.medialert.ui.home.HomeViewModel
import com.fernanda.medialert.ui.home.HomeViewModelFactory
import com.fernanda.medialert.ui.theme.MediAlertTheme

class HistorialActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels {
        val app = application as MediAlertApp
        HomeViewModelFactory(app.historialRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idUsuario = intent.getIntExtra("ID_USUARIO", 1)

        setContent {
            MediAlertTheme {
                val historial by viewModel.historial.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.cargarDatosHome(idUsuario)
                }

                HistorialScreen(
                    historial = historial,
                    onVolver = { finish() }
                )
            }
        }
    }
}

@Composable
fun HistorialScreen(historial: List<HistorialResponse>, onVolver: () -> Unit) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(azulClaro, azulFuerte)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text("Historial de Tomas", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes registros de tomas", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historial) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.exitoso),
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(item.nombre_medicamento, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Fecha: ${item.fecha_hora_real}", fontSize = 14.sp, color = Color.Gray)
                                Text("Estado: ${item.estado}", fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}


