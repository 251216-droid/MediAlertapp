package com.fernanda.medialert.ui.enfermedades

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.fernanda.medialert.MediAlertApp
import com.fernanda.medialert.data.local.entity.Enfermedad
import com.fernanda.medialert.ui.perfil.PerfilActivity
import com.fernanda.medialert.ui.theme.MediAlertTheme

class EnfermedadesActivity : ComponentActivity() {

    private val viewModel: EnfermedadViewModel by viewModels {
        EnfermedadViewModelFactory((application as MediAlertApp).enfermedadRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        val nombre    = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val correo    = intent.getStringExtra("CORREO_USUARIO") ?: ""

        setContent {
            MediAlertTheme {
                var pantallaActual    by remember { mutableStateOf("lista") }
                var enfermedadEditar  by remember { mutableStateOf<Enfermedad?>(null) }

                when (pantallaActual) {
                    "lista" -> EnfermedadesScreen(
                        idUsuario      = idUsuario,
                        viewModel      = viewModel,
                        onAgregarClick = {
                            enfermedadEditar = null
                            pantallaActual = "registro"
                        },
                        onEditarClick  = { enf ->
                            enfermedadEditar = enf
                            pantallaActual = "registro"
                        },
                        onVolver       = { finish() },
                        onIrAPerfil    = {
                            startActivity(Intent(this, PerfilActivity::class.java).apply {
                                putExtra("ID_USUARIO", idUsuario)
                                putExtra("NOMBRE_USUARIO", nombre)
                                putExtra("CORREO_USUARIO", correo)
                            })
                        }
                    )

                    "registro" -> RegistrarEnfermedadScreen(
                        idUsuario         = idUsuario,
                        viewModel         = viewModel,
                        enfermedadAEditar = enfermedadEditar,
                        onVolver          = {
                            // Limpiar el mensaje para que LaunchedEffect pueda dispararse de nuevo
                            viewModel.limpiarMensaje()
                            pantallaActual = "lista"
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val idUsuario = intent.getIntExtra("ID_USUARIO", -1)
        if (idUsuario != -1) viewModel.cargarEnfermedades(idUsuario)
    }
}


