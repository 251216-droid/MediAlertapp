package com.example.medicare.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare.data.local.entity.Medicamento

@Composable
fun MedicamentosScreen(
    idUsuario: Int,
    viewModel: MedicamentoViewModel,
    onAgregarClick: () -> Unit,
    onEditarClick: (Medicamento) -> Unit,
    onVolver: () -> Unit // AGREGADO: Función para volver
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val medicamentos by viewModel.medicamentos.collectAsState()
    
    var medicamentoAEliminar by remember { mutableStateOf<Medicamento?>(null) }

    LaunchedEffect(key1 = idUsuario) {
        viewModel.cargarMedicamentos(idUsuario)
    }

    if (medicamentoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { medicamentoAEliminar = null },
            title = { Text("Eliminar Medicamento") },
            text = { Text("¿Estás seguro de que deseas eliminar ${medicamentoAEliminar?.nombreMedicamento}?") },
            confirmButton = {
                TextButton(onClick = {
                    medicamentoAEliminar?.let { viewModel.eliminarMedicamento(it, idUsuario) }
                    medicamentoAEliminar = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White, // CAMBIADO: Fondo blanco
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(azulClaro, azulFuerte)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 40.dp, bottom = 24.dp, start = 12.dp, end = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AGREGADO: Flecha de regresar
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Medicamentos",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${medicamentos.size} Medicamentos registrados",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    
                    IconButton(onClick = { /* Ir a Perfil */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarClick,
                containerColor = azulFuerte,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White) // CAMBIADO: Aseguramos fondo blanco
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(medicamentos) { med ->
                MedicamentoCard(
                    med = med,
                    onEdit = { onEditarClick(med) },
                    onDelete = { medicamentoAEliminar = med }
                )
            }
        }
    }
}

@Composable
fun MedicamentoCard(
    med: Medicamento,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val azul = Color(0xFF0086FF)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${med.nombreMedicamento} - ${med.tipoPresentacion} ${med.dosis}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Categoría: ${med.categoria}", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "Estado: ${med.estadoMedicamento}", fontSize = 14.sp, color = Color.Gray)
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = azul, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
