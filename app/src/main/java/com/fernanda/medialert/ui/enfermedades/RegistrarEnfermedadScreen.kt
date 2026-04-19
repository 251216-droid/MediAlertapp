package com.fernanda.medialert.ui.enfermedades

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.fernanda.medialert.data.local.entity.Enfermedad
import java.util.Calendar

@Composable
fun RegistrarEnfermedadScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel,
    enfermedadAEditar: Enfermedad? = null,
    onVolver: () -> Unit
) {
    val azulClaro = Color(0xFF66B2FF)
    val azulFuerte = Color(0xFF0086FF)
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Precargar campos al editar
    var nombre      by remember { mutableStateOf(enfermedadAEditar?.nombreEnfermedad ?: "") }
    var fecha       by remember { mutableStateOf(enfermedadAEditar?.fechaDiagnostico ?: "") }
    var descripcion by remember { mutableStateOf(enfermedadAEditar?.descripcion ?: "") }
    var guardando   by remember { mutableStateOf(false) }

    // Cuando el mensaje cambia a "guardado/actualizado", volver atrás
    LaunchedEffect(mensaje) {
        if (mensaje != null && (mensaje!!.contains("guardada", ignoreCase = true) || mensaje!!.contains("actualizada", ignoreCase = true))) {
            onVolver()
            viewModel.limpiarMensaje()
        }
    }

    // DatePicker nativo — abre calendario del sistema
    fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        if (fecha.isNotBlank()) {
            try {
                val p = fecha.split("/")
                if (p.size == 3) {
                    cal.set(Calendar.DAY_OF_MONTH, p[0].trim().toInt())
                    cal.set(Calendar.MONTH, p[1].trim().toInt() - 1)
                    cal.set(Calendar.YEAR, p[2].trim().toInt())
                }
            } catch (_: Exception) { }
        }
        DatePickerDialog(context, { _, year, month, day ->
            fecha = "${String.format("%02d", day)}/${String.format("%02d", month + 1)}/$year"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())
    ) {
        // ── HEADER AZUL ──────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(azulClaro, azulFuerte)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver, enabled = !isLoading) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column {
                    Text(
                        text = if (enfermedadAEditar == null) "Registrar Enfermedad" else "Editar Enfermedad",
                        color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (enfermedadAEditar == null) "Completa los datos de la enfermedad" else "Modifica los datos",
                        color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp
                    )
                }
            }
        }

        // ── FORMULARIO ────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Nombre ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Nombre de la enfermedad *",
                        color = azulFuerte, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp
                    )
                    Text("Ej: Diabetes tipo 2, Hipertensión...", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                        singleLine = true
                    )
                }
            }

            // ── Fecha de diagnóstico (calendario nativo) ─────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Fecha de diagnóstico",
                        color = azulFuerte, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp
                    )
                    Text("Toca el ícono del calendario para seleccionar", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                        trailingIcon = {
                            IconButton(onClick = { mostrarDatePicker() }) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Seleccionar fecha",
                                    tint = azulFuerte
                                )
                            }
                        }
                    )
                }
            }

            // ── Descripción ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Descripción / Notas",
                        color = azulFuerte, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp
                    )
                    Text("Síntomas, medicamentos, observaciones...", color = Color(0xFF64B5F6), fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Botones ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onVolver,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0), // Gris
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0).copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) { Text("Cancelar", fontWeight = FontWeight.Bold) }

                Button(
                    onClick = {
                        if (nombre.isBlank()) return@Button
                        if (enfermedadAEditar == null) {
                            viewModel.guardarEnfermedad(idUsuario, nombre.trim(), fecha, descripcion.trim())
                        } else {
                            viewModel.actualizarEnfermedad(
                                enfermedadAEditar.idEnfermedad, idUsuario,
                                nombre.trim(), fecha, descripcion.trim()
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66B2FF), // Azul claro
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF66B2FF).copy(alpha = 0.3f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    enabled = !isLoading && nombre.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (enfermedadAEditar == null) "Guardar" else "Actualizar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

