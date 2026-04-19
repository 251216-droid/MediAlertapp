package com.example.medicare.ui.enfermedades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun RegistrarEnfermedadScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel,
    enfermedadAEditar: Enfermedad? = null,
    onVolver: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    
    var nombre by remember { mutableStateOf(enfermedadAEditar?.nombreEnfermedad ?: "") }
    var fecha by remember { mutableStateOf(enfermedadAEditar?.fechaDiagnostico ?: "") }
    var descripcion by remember { mutableStateOf(enfermedadAEditar?.descripcion ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
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
                Column {
                    Text(
                        text = if (enfermedadAEditar == null) "Registrar Enfermedad" else "Editar Enfermedad",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (enfermedadAEditar == null) "Agrega una nueva enfermedad" else "Modifica los datos",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(text = "Nombre de la enfermedad:", color = azulFuerte, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column {
                Text(text = "Fecha de diagnóstico:", color = azulFuerte, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = fecha, onValueChange = { fecha = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column {
                Text(text = "Notas adicionales:", color = azulFuerte, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        if (nombre.isNotBlank()) {
                            if (enfermedadAEditar == null) {
                                viewModel.guardarEnfermedad(idUsuario, nombre, fecha, descripcion)
                            } else {
                                viewModel.actualizarEnfermedad(enfermedadAEditar.idEnfermedad, idUsuario, nombre, fecha, descripcion)
                            }
                            onVolver()
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulFuerte)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
