package com.fernanda.medialert.ui.enfermedades

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
import com.fernanda.medialert.data.local.entity.Enfermedad

@Composable
fun EnfermedadesScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel,
    onAgregarClick: () -> Unit,
    onEditarClick: (Enfermedad) -> Unit,
    onVolver: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val enfermedades by viewModel.enfermedades.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var enfermedadAEliminar by remember { mutableStateOf<Enfermedad?>(null) }

    // Cargar al iniciar
    LaunchedEffect(idUsuario) {
        viewModel.cargarEnfermedades(idUsuario)
    }

    // Diálogo de confirmación para eliminar
    if (enfermedadAEliminar != null) {
        AlertDialog(
            onDismissRequest = { enfermedadAEliminar = null },
            title = { Text("Eliminar enfermedad", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que deseas eliminar \"${enfermedadAEliminar?.nombreEnfermedad}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        enfermedadAEliminar?.let {
                            viewModel.eliminarEnfermedad(it.idEnfermedad, idUsuario)
                        }
                        enfermedadAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66B2FF), // Azul
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { enfermedadAEliminar = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0), // Gris claro
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(azulClaro, azulFuerte)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 40.dp, bottom = 24.dp, start = 12.dp, end = 24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enfermedades", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (isLoading) "Cargando..." else "${enfermedades.size} registradas",
                            color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = onIrAPerfil) {
                        Icon(Icons.Default.AccountCircle, "Perfil", tint = Color.White, modifier = Modifier.size(32.dp))
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
            ) { Icon(Icons.Default.Add, "Agregar") }
        }
    ) { padding ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = azulFuerte)
            }
        } else if (enfermedades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Sin enfermedades registradas", color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text("Toca el botón + para agregar", color = Color(0xFFBDBDBD), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                items(enfermedades, key = { it.idEnfermedad }) { enfermedad ->
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
fun EnfermedadCard(enfermedad: Enfermedad, onEdit: () -> Unit, onDelete: () -> Unit) {
    val azul = Color(0xFF0086FF)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra de color lateral
            Box(modifier = Modifier.size(width = 4.dp, height = 48.dp).background(azul, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(16.dp))
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    enfermedad.nombreEnfermedad,
                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray
                )
                if (enfermedad.fechaDiagnostico.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        " Diagnóstico: ${enfermedad.fechaDiagnostico}",
                        fontSize = 13.sp, color = Color.Gray
                    )
                }
                if (enfermedad.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        enfermedad.descripcion,
                        fontSize = 13.sp, color = Color(0xFF9E9E9E),
                        maxLines = 2
                    )
                }
            }
            // Botones
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar", tint = azul, modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

