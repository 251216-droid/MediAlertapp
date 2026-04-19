package com.fernanda.medialert.ui.medicamentos

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fernanda.medialert.data.local.entity.Medicamento
import java.util.Calendar

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
    val context = LocalContext.current

    // Obtener datos de programación si estamos editando
    val progData = remember(medicamentoAEditar?.idMedicamento) {
        medicamentoAEditar?.let { viewModel.obtenerProgramacionDeMedicamento(it.idMedicamento) }
    }

    // Separar dosis guardada en sus partes: "1 tableta (500mg)" → cant="1 tableta", mg="500mg"
    fun separarDosis(dosis: String): Pair<String, String> {
        val regex = Regex("""^(.*?)\s*\(([^)]+)\)$""")
        val match = regex.find(dosis.trim())
        return if (match != null) {
            Pair(match.groupValues[1].trim(), match.groupValues[2].trim())
        } else {
            Pair(dosis.trim(), "")
        }
    }

    val (dosisInicialCant, dosisInicialMg) = remember(medicamentoAEditar?.dosis) {
        separarDosis(medicamentoAEditar?.dosis ?: "")
    }

    var nombre      by remember { mutableStateOf(medicamentoAEditar?.nombreMedicamento ?: "") }
    var tipo        by remember { mutableStateOf(medicamentoAEditar?.tipoPresentacion ?: "") }
    var dosisMg     by remember { mutableStateOf(dosisInicialMg) }
    var dosisCant   by remember { mutableStateOf(dosisInicialCant) }
    var frecuencia  by remember { mutableStateOf(progData?.frecuencia_horas?.toString() ?: "") }
    var primeraToma by remember { mutableStateOf(progData?.hora_primera_toma ?: "") }
    var duracion    by remember { mutableStateOf("") }
    var expanded    by remember { mutableStateOf(false) }

    val tipos = listOf("Tableta", "Jarabe", "Cápsula", "Inyección", "Gotas", "Suspensión")

    // TimePicker en formato 12h
    fun mostrarTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(context, { _, hour, minute ->
            val ampm = if (hour < 12) "AM" else "PM"
            val h12 = when {
                hour == 0  -> 12
                hour > 12  -> hour - 12
                else       -> hour
            }
            primeraToma = "$h12:${String.format("%02d", minute)} $ampm"
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {

        Box(
            modifier = Modifier.fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(azulClaro, azulFuerte)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column {
                    Text(
                        text = if (medicamentoAEditar == null) "Registrar Medicamento" else "Editar Medicamento",
                        color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                    if (medicamentoAEditar != null) {
                        Text("Modifica los datos del medicamento", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }
        }


        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Nombre
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre del medicamento:", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Ej: Paracetamol, Ibuprofeno", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            // Tipo (dropdown)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tipo de medicamento:", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Selecciona la presentación", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = tipo, onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = azulFuerte) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            tipos.forEach { t -> DropdownMenuItem(text = { Text(t) }, onClick = { tipo = t; expanded = false }) }
                        }
                    }
                }
            }

            // Concentración (mg)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Concentración:", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Ej: 500mg, 250mg", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dosisMg, onValueChange = { dosisMg = it },
                        placeholder = { Text("Ej: 500mg") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            // Dosis por toma
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dosis por toma:", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Ej: 1 tableta, 2 cápsulas, 5ml", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = dosisCant, onValueChange = { dosisCant = it },
                        placeholder = { Text("Ej: 1 tableta") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            // Frecuencia
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Frecuencia (cada cuántas horas):", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Ej: 8 (cada 8 horas)", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = frecuencia, onValueChange = { frecuencia = it },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ej: 8") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Primera toma:", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("¿A qué hora empieza el tratamiento?", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = primeraToma, onValueChange = {}, readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Toca el reloj para seleccionar") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                        trailingIcon = {
                            IconButton(onClick = { mostrarTimePicker() }) {
                                Icon(Icons.Default.AccessTime, null, tint = azulFuerte)
                            }
                        }
                    )
                }
            }

            // Duración
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Duración del tratamiento (días):", color = azulFuerte, fontWeight = FontWeight.ExtraBold)
                    Text("Opcional: días totales de tratamiento", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = duracion, onValueChange = { duracion = it },
                        placeholder = { Text("Ej: 7") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            // Botones
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onVolver,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFff6961),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // Construir dosis final
                        val dosisFinal = when {
                            dosisMg.isNotBlank() && dosisCant.isNotBlank() -> "$dosisCant (${dosisMg})"
                            dosisCant.isNotBlank() -> dosisCant
                            else -> dosisMg
                        }
                        val frecInt = frecuencia.filter { it.isDigit() }.toIntOrNull() ?: 8
                        val duracionDias = duracion.filter { it.isDigit() }.toIntOrNull() ?: 0

                        if (medicamentoAEditar == null) {
                            // NUEVO MEDICAMENTO
                            viewModel.registrarMedicamentoConHorario(
                                idUsuario, nombre, tipo, dosisFinal, "General", "Activo",
                                primeraToma, frecInt, "Todos", duracionDias
                            )
                        } else {
                            // EDITAR — actualiza medicamento + programación
                            viewModel.editarMedicamentoCompleto(
                                medicamentoAEditar.idMedicamento, idUsuario,
                                nombre, tipo, dosisFinal, "General", "Activo",
                                primeraToma, frecInt, duracionDias
                            )
                        }
                        onVolver()
                    },
                    modifier = Modifier.weight(1.5f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulFuerte, // Azul
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (medicamentoAEditar == null) "Guardar" else "Actualizar", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

