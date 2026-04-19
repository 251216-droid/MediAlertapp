package com.example.medicare.ui.medicamentos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMedicamentoScreen(
    idUsuario: Int,
    viewModel: MedicamentoViewModel,
    medicamentoAEditar: Medicamento? = null,
    onVolver: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    
    var nombre by remember { mutableStateOf(medicamentoAEditar?.nombreMedicamento ?: "") }
    var tipo by remember { mutableStateOf(medicamentoAEditar?.tipoPresentacion ?: "") }
    
    // CAMPOS SEPARADOS SEGÚN TU PETICIÓN
    var dosisMg by remember { mutableStateOf("") } // Ej: 500mg
    var dosisCant by remember { mutableStateOf("") } // Ej: 1 tableta
    
    var contenidoTotal by remember { mutableStateOf("") } // Ej: Caja con 30
    var frecuencia by remember { mutableStateOf("") } 
    var primeraToma by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    val tipos = listOf("Tableta", "Jarabe", "Cápsula", "Inyección", "Gotas", "Suspensión")

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())
    ) {
        // Header MediAlert
        Box(
            modifier = Modifier.fillMaxWidth().background(brush = Brush.verticalGradient(colors = listOf(azulClaro, azulFuerte)), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Text(text = if (medicamentoAEditar == null) "Registrar Medicamento" else "Editar Medicamento", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Nombre del medicamento:", color = azulFuerte, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

            Text("Tipo de medicamento:", color = azulFuerte, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = tipo, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(), trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }, shape = RoundedCornerShape(12.dp))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    tipos.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { tipo = it; expanded = false }) }
                }
            }

            // CONTENEDOR 1: CONCENTRACIÓN (mg/g)
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Concentración", color = Color(0xFF388E3C), fontWeight = FontWeight.ExtraBold)
                    OutlinedTextField(value = dosisMg, onValueChange = { dosisMg = it }, placeholder = { Text("Ej: 500mg") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White))
                }
            }

            // CONTENEDOR 2: DOSIS POR TOMA
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dosis por toma", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    OutlinedTextField(value = dosisCant, onValueChange = { dosisCant = it }, placeholder = { Text("Ej: 1 tableta") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White))
                }
            }

            // CONTENEDOR 3: CONTENIDO TOTAL
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Contenido Total", color = Color(0xFFE65100), fontWeight = FontWeight.ExtraBold)
                    OutlinedTextField(value = contenidoTotal, onValueChange = { contenidoTotal = it }, placeholder = { Text("Ej: Caja con 30 unidades") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White))
                }
            }

            Text("Frecuencia:", color = azulFuerte, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = frecuencia, onValueChange = { frecuencia = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Cada 8h") }, shape = RoundedCornerShape(12.dp))

            Text("Primera toma:", color = azulFuerte, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = primeraToma, onValueChange = { primeraToma = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("10:30 AM") }, shape = RoundedCornerShape(12.dp))

            Text("Duración:", color = azulFuerte, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = duracion, onValueChange = { duracion = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("7 días") }, shape = RoundedCornerShape(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f).height(50.dp)) { Text("Cancelar") }
                Button(
                    onClick = {
                        val dosisFinal = "$dosisCant ($dosisMg)"
                        if (medicamentoAEditar == null) {
                            val frecInt = frecuencia.filter { it.isDigit() }.toIntOrNull() ?: 8
                            viewModel.registrarMedicamentoConHorario(idUsuario, nombre, tipo, dosisFinal, "General", "Activo", primeraToma, frecInt, duracion)
                        } else {
                            viewModel.editarMedicamento(medicamentoAEditar.idMedicamento, idUsuario, nombre, tipo, dosisFinal, "General", "Activo")
                        }
                        onVolver()
                    },
                    modifier = Modifier.weight(1.5f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulFuerte)
                ) { Text(if (medicamentoAEditar == null) "Guardar" else "Actualizar", fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
