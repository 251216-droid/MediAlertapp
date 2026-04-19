package com.fernanda.medialert.data.repositories

import android.util.Log
import com.fernanda.medialert.data.local.dao.UsuarioDao
import com.fernanda.medialert.data.local.entity.Usuario
import com.fernanda.medialert.data.remote.ActualizarPerfilRequest
import com.fernanda.medialert.data.remote.AuthApiService
import com.fernanda.medialert.data.remote.FcmTokenRequest
import com.fernanda.medialert.data.remote.LoginRequest
import com.fernanda.medialert.data.remote.RegistroRequest

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: AuthApiService
) {
    companion object {
        private const val TAG = "UsuarioRepository"
    }

    suspend fun registrarUsuario(usuario: Usuario): Int {
        try {
            val request = RegistroRequest(usuario.nombre, usuario.correo, usuario.password)
            val response = apiService.registrarUsuario(request)

            if (response.isSuccessful && response.body()?.idUsuario != null) {
                val body = response.body()!!
                val realId = body.idUsuario!!
                val usuarioConIdReal = Usuario(
                    realId,
                    body.nombre ?: usuario.nombre,
                    body.correo ?: usuario.correo,
                    usuario.password
                )
                usuarioDao.insertarUsuario(usuarioConIdReal)
                return realId
            }

            Log.e(TAG, "Registro fallido. code=${response.code()} error=${response.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.e(TAG, "Excepcion registrando usuario", e)
        }

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

            Log.e(TAG, "Login fallido. code=${response.code()} error=${response.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.e(TAG, "Excepcion iniciando sesion", e)
        }

        return usuarioDao.iniciarSesion(correo, password)
    }

    suspend fun actualizarPerfil(idUsuario: Int, nombre: String, correo: String, password: String): Boolean {
        return try {
            val request = ActualizarPerfilRequest(nombre, correo, password)
            val response = apiService.actualizarPerfil(idUsuario, request)
            if (response.isSuccessful) {
                val usuarioActual = usuarioDao.obtenerPorId(idUsuario)
                if (usuarioActual != null) {
                    val pass = if (password.isNotEmpty()) password else usuarioActual.password
                    usuarioDao.insertarUsuario(
                        usuarioActual.copy(nombre = nombre, correo = correo, password = pass)
                    )
                }
                true
            } else {
                Log.e(TAG, "Actualizar perfil fallido. code=${response.code()} error=${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepcion actualizando perfil", e)
            false
        }
    }

    suspend fun actualizarFcmToken(idUsuario: Int, token: String): Boolean {
        return try {
            val response = apiService.actualizarFcmToken(idUsuario, FcmTokenRequest(token))
            if (!response.isSuccessful) {
                Log.e(TAG, "Actualizar FCM fallido. code=${response.code()} error=${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Excepcion actualizando token FCM", e)
            false
        }
    }
}
