package com.example.medicare.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.data.repositories.UsuarioRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {
    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado = _usuarioLogueado

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    fun iniciarSesion(correo: String, password: String) {
        viewModelScope.launch {
            try {
                val usuario = repository.iniciarSesion(correo, password)
                if (usuario != null) {
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
                // CORRECCIÓN: El campo en Usuario.kt es 'password', no 'contrasena'
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    password = password
                )
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
