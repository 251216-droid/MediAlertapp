package com.fernanda.medialert.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernanda.medialert.data.local.entity.Medicamento

@Composable
fun MedicamentosScreen(
    idUsuario: Int,
    viewModel: MedicamentoViewModel,
    onAgregarClick: () -> Unit,
    onEditarClick: (Medicamento) -> Unit,
    onVolver: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val medicamentos by viewModel.medicamentos.collectAsState()
    var medicamentoAEliminar by remember { mutableStateOf<Medicamento?>(null) }

    LaunchedEffect(idUsuario) { viewModel.cargarMedicamentos(idUsuario) }

    if (medicamentoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { medicamentoAEliminar = null },
            title = { Text("Eliminar Medicamento") },
            text = { Text("¿Eliminar ${medicamentoAEliminar?.nombreMedicamento}?") },
            confirmButton = {
                TextButton(onClick = {
                    medicamentoAEliminar?.let { viewModel.eliminarMedicamento(it, idUsuario) }
                    medicamentoAEliminar = null
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(brush = Brush.verticalGradient(colors = listOf(azulClaro, azulFuerte)), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(top = 40.dp, bottom = 24.dp, start = 12.dp, end = 24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Medicamentos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("${medicamentos.size} registrados", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    IconButton(onClick = onIrAPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick, containerColor = azulFuerte, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        if (medicamentos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Sin medicamentos registrados", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Color.White).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(medicamentos) { med ->
                    MedicamentoCard(med = med, onEdit = { onEditarClick(med) }, onDelete = { medicamentoAEliminar = med })
                }
            }
        }
    }
}

@Composable
fun MedicamentoCard(med: Medicamento, onEdit: () -> Unit, onDelete: () -> Unit) {
    val azul = Color(0xFF0086FF)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${med.nombreMedicamento}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.DarkGray)
                    Text("${med.tipoPresentacion} · ${med.dosis}", fontSize = 14.sp, color = azul)
                    Text("Estado: ${med.estadoMedicamento}", fontSize = 13.sp, color = Color.Gray)
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = azul, modifier = Modifier.size(24.dp)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(24.dp)) }
                }
            }
        }
    }
}

