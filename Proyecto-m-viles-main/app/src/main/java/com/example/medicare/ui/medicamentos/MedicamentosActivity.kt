package com.example.medicare.ui.medicamentos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.medicare.MedicareApp
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.ui.theme.MediCareTheme

class MedicamentosActivity : ComponentActivity() {

    private val viewModel: MedicamentoViewModel by viewModels {
        MedicamentoViewModelFactory((application as MedicareApp).medicamentoRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)

        setContent {
            MediCareTheme {
                var pantallaActual by remember { mutableStateOf("lista") }
                var medicamentoAEditar by remember { mutableStateOf<Medicamento?>(null) }

                when (pantallaActual) {
                    "lista" -> {
                        MedicamentosScreen(
                            idUsuario = idUsuario,
                            viewModel = viewModel,
                            onAgregarClick = { 
                                medicamentoAEditar = null
                                pantallaActual = "registro" 
                            },
                            onEditarClick = { medicamento ->
                                medicamentoAEditar = medicamento
                                pantallaActual = "registro"
                            },
                            onVolver = { finish() } // AGREGADO: Cerrar para volver al Home
                        )
                    }
                    "registro" -> {
                        RegistrarMedicamentoScreen(
                            idUsuario = idUsuario,
                            viewModel = viewModel,
                            medicamentoAEditar = medicamentoAEditar,
                            onVolver = {
                                pantallaActual = "lista"
                            }
                        )
                    }
                }
            }
        }
    }
}
