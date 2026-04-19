package com.example.medicare.ui.enfermedades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.medicare.MedicareApp
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.ui.theme.MediCareTheme

class EnfermedadesActivity : ComponentActivity() {

    private val viewModel: EnfermedadViewModel by viewModels {
        EnfermedadViewModelFactory((application as MedicareApp).enfermedadRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)

        setContent {
            MediCareTheme {
                var pantallaActual by remember { mutableStateOf("historial") }
                var enfermedadAEditar by remember { mutableStateOf<Enfermedad?>(null) }

                when (pantallaActual) {
                    "historial" -> {
                        EnfermedadesScreen(
                            idUsuario = idUsuario,
                            viewModel = viewModel,
                            onAgregarClick = { 
                                enfermedadAEditar = null
                                pantallaActual = "registro" 
                            },
                            onEditarClick = { enfermedad ->
                                enfermedadAEditar = enfermedad
                                pantallaActual = "registro"
                            },
                            onVolver = { finish() }
                        )
                    }
                    "registro" -> {
                        RegistrarEnfermedadScreen(
                            idUsuario = idUsuario,
                            viewModel = viewModel,
                            enfermedadAEditar = enfermedadAEditar,
                            onVolver = {
                                pantallaActual = "historial"
                            }
                        )
                    }
                }
            }
        }
    }
}
