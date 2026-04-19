package com.fernanda.medialert.ui.auth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernanda.medialert.data.local.entity.Usuario
import com.fernanda.medialert.data.repositories.UsuarioRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val repository: UsuarioRepository,
    private val context: Context
) : ViewModel() {

    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado = _usuarioLogueado

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    fun iniciarSesion(correo: String, password: String) {
        viewModelScope.launch {
            try {
                val usuario = repository.iniciarSesion(correo, password)
                if (usuario != null) {
                    // Guardar idUsuario en SharedPreferences para que el servicio FCM lo use
                    val prefs = context.getSharedPreferences("medialert_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putInt("idUsuario", usuario.idUsuario).apply()

                    // Enviar token FCM al servidor (o el pendiente si ya había uno)
                    val tokenPendiente = prefs.getString("fcm_token_pendiente", null)
                    if (tokenPendiente != null) {
                        repository.actualizarFcmToken(usuario.idUsuario, tokenPendiente)
                        prefs.edit().remove("fcm_token_pendiente").apply()
                    } else {
                        // Obtener token actual de Firebase
                        try {
                            val token = FirebaseMessaging.getInstance().token.await()
                            repository.actualizarFcmToken(usuario.idUsuario, token)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    _usuarioLogueado.value = usuario
                } else {
                    _mensajeError.value = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun registrarUsuario(nombre: String, correo: String, password: String) {
        viewModelScope.launch {
            try {
                val nuevoUsuario = Usuario(nombre = nombre, correo = correo, password = password)
                val id = repository.registrarUsuario(nuevoUsuario)
                if (id > 0) {
                    _usuarioLogueado.value = nuevoUsuario
                } else {
                    _mensajeError.value = "Error al registrar usuario"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error en el registro: ${e.message}"
            }
        }
    }
}

