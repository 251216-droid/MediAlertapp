package com.fernanda.medialert.ui.medicamentos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.data.local.entity.Medicamento
import com.fernanda.medialert.ui.perfil.PerfilActivity
import com.fernanda.medialert.ui.theme.MediAlertTheme

class MedicamentosActivity : ComponentActivity() {

    private val viewModel: MedicamentoViewModel by viewModels {
        MedicamentoViewModelFactory((application as MediAlertApp).medicamentoRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        val nombre = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val correo = intent.getStringExtra("CORREO_USUARIO") ?: ""

        setContent {
            MediAlertTheme {
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
                            onVolver = { finish() },
                            onIrAPerfil = {
                                startActivity(Intent(this, PerfilActivity::class.java).apply {
                                    putExtra("ID_USUARIO", idUsuario)
                                    putExtra("NOMBRE_USUARIO", nombre)
                                    putExtra("CORREO_USUARIO", correo)
                                })
                            }
                        )
                    }
                    "registro" -> {
                        RegistrarMedicamentoScreen(
                            idUsuario = idUsuario,
                            viewModel = viewModel,
                            medicamentoAEditar = medicamentoAEditar,
                            onVolver = { pantallaActual = "lista" }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        if (idUsuario != -1) viewModel.cargarMedicamentos(idUsuario)
    }
}


