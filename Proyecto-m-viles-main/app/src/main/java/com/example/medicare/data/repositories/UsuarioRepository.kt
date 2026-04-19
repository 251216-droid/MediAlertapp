package com.example.medicare.data.repositories

import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.data.remote.AuthApiService
import com.example.medicare.data.remote.LoginRequest
import com.example.medicare.data.remote.RegistroRequest

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
                // Guardamos en Room con el ID real de MySQL y el nombre real
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
                // CORRECCIÓN: Usar body.nombre en lugar de body.mensaje
                val usuario = Usuario(
                    idUsuario = body.idUsuario!!,
                    nombre = body.nombre ?: "Usuario", 
                    correo = body.correo ?: correo,
                    password = password
                )
                usuarioDao.insertarUsuario(usuario) // Actualizar local
                return usuario
            }
        } catch (e: Exception) { e.printStackTrace() }
        return usuarioDao.iniciarSesion(correo, password)
    }
}
