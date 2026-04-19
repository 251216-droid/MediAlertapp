package com.example.medicare.ui.enfermedades

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
import com.example.medicare.data.local.entity.Enfermedad

@Composable
fun EnfermedadesScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel,
    onAgregarClick: () -> Unit,
    onEditarClick: (Enfermedad) -> Unit,
    onVolver: () -> Unit // AGREGADO: Flecha de regreso
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val enfermedades by viewModel.enfermedades.collectAsState()
    
    var enfermedadAEliminar by remember { mutableStateOf<Enfermedad?>(null) }

    LaunchedEffect(key1 = idUsuario) {
        viewModel.cargarEnfermedades(idUsuario)
    }

    if (enfermedadAEliminar != null) {
        AlertDialog(
            onDismissRequest = { enfermedadAEliminar = null },
            title = { Text("Eliminar Enfermedad") },
            text = { Text("¿Estás seguro de que deseas eliminar ${enfermedadAEliminar?.nombreEnfermedad}?") },
            confirmButton = {
                TextButton(onClick = {
                    enfermedadAEliminar?.let { viewModel.eliminarEnfermedad(it.idEnfermedad, idUsuario) }
                    enfermedadAEliminar = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { enfermedadAEliminar = null }) {
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
                            text = "Enfermedades",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${enfermedades.size} Enfermedades registradas",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = { /* Perfil */ }) {
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
        if (enfermedades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin enfermedades registradas",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White) // CAMBIADO: Aseguramos fondo blanco
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(enfermedades) { enfermedad ->
                    EnfermedadCard(
                        enfermedad = enfermedad,
                        onEdit = { onEditarClick(enfermedad) },
                        onDelete = { enfermedadAEliminar = enfermedad }
                    )
                }
            }
        }
    }
}

@Composable
fun EnfermedadCard(
    enfermedad: Enfermedad,
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
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 40.dp)
                        .background(azul, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = enfermedad.nombreEnfermedad,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    if (enfermedad.fechaDiagnostico.isNotBlank()) {
                        Text(
                            text = "Diagnosticado el: ${enfermedad.fechaDiagnostico}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
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
