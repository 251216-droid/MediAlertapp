package com.fernanda.medialert.data.repositories

import com.fernanda.medialert.data.local.dao.UsuarioDao
import com.fernanda.medialert.data.local.entity.Usuario
import com.fernanda.medialert.data.remote.AuthApiService
import com.fernanda.medialert.data.remote.LoginRequest
import com.fernanda.medialert.data.remote.RegistroRequest
import com.fernanda.medialert.data.remote.ActualizarPerfilRequest
import com.fernanda.medialert.data.remote.FcmTokenRequest

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: AuthApiService
) {
    suspend fun registrarUsuario(usuario: Usuario): Int {
        try {
            val request = RegistroRequest(usuario.nombre, usuario.correo, usuario.password)
            val response = apiService.registrarUsuario(request)
            
            if (response.isSuccessful && response.body()?.idUsuario != null) {
                val body = response.body()!!
                val realId = body.idUsuario!!
                val usuarioConIdReal = Usuario(realId, body.nombre ?: usuario.nombre, body.correo ?: usuario.correo, usuario.password)
                usuarioDao.insertarUsuario(usuarioConIdReal)
                return realId
            }
        } catch (e: Exception) { e.printStackTrace() }
        return -1
    }

    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        try {
            val response = apiService.loginUsuario(LoginRequest(correo, password))
            if (response.isSuccessful && response.body()?.idUsuario != null) {
                val body = response.body()!!
                val usuario = Usuario(
                    idUsuario = body.idUsuario!!,
                    nombre = body.nombre ?: "Usuario", 
                    correo = body.correo ?: correo,
                    password = password
                )
                usuarioDao.insertarUsuario(usuario)
                return usuario
            }
        } catch (e: Exception) { e.printStackTrace() }
        return usuarioDao.iniciarSesion(correo, password)
    }

    suspend fun actualizarPerfil(idUsuario: Int, nombre: String, correo: String, password: String): Boolean {
        return try {
            val request = ActualizarPerfilRequest(nombre, correo, password)
            val response = apiService.actualizarPerfil(idUsuario, request)
            if (response.isSuccessful) {
                // Actualizar en Room también
                val usuarioActual = usuarioDao.obtenerPorId(idUsuario)
                if (usuarioActual != null) {
                    val pass = if (password.isNotEmpty()) password else usuarioActual.password
                    usuarioDao.insertarUsuario(usuarioActual.copy(nombre = nombre, correo = correo, password = pass))
                }
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    // ── FCM: Enviar token al servidor para recibir notificaciones push ────────
    suspend fun actualizarFcmToken(idUsuario: Int, token: String): Boolean {
        return try {
            val response = apiService.actualizarFcmToken(idUsuario, FcmTokenRequest(token))
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

