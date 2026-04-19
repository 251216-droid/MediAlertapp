package com.example.medicare.data.repositories

import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.data.remote.EnfermedadApiService
import com.example.medicare.data.remote.EnfermedadRequest

class EnfermedadRepository(
    private val enfermedadDao: EnfermedadDao,
    private val apiService: EnfermedadApiService
) {
    suspend fun agregarEnfermedad(idUsuario: Int, nombre: String, fecha: String, descripcion: String) {
        // 1. Guardar LOCAL primero para que se vea en la pantalla
        val enfermedadLocal = Enfermedad(
            idUsuarioFk = idUsuario,
            nombreEnfermedad = nombre,
            fechaDiagnostico = fecha,
            descripcion = descripcion
        )
        enfermedadDao.insert(enfermedadLocal)

        // 2. Intentar sincronizar con la API de fondo
        try {
            val request = EnfermedadRequest(
                idUsuario = idUsuario,
                nombreEnfermedad = nombre,
                fechaDiagnostico = fecha,
                descripcion = descripcion
            )
            apiService.agregarEnfermedad(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun editarEnfermedad(idEnfermedad: Int, idUsuario: Int, nombre: String, fecha: String, descripcion: String): Boolean {
        val enfermedadEditada = Enfermedad(
            idEnfermedad = idEnfermedad,
            idUsuarioFk = idUsuario,
            nombreEnfermedad = nombre,
            fechaDiagnostico = fecha,
            descripcion = descripcion
        )
        
        // Actualizar localmente primero
        enfermedadDao.actualizar(enfermedadEditada)

        // Sincronizar con el servidor
        return try {
            val request = EnfermedadRequest(
                idUsuario = idUsuario,
                nombreEnfermedad = nombre,
                fechaDiagnostico = fecha,
                descripcion = descripcion
            )
            val response = apiService.editarEnfermedad(idEnfermedad, request)
            response.isSuccessful
        } catch (e: Exception) {
            true // Retornamos true porque ya se actualizó localmente
        }
    }

    suspend fun eliminarEnfermedad(id: Int): Boolean {
        // 1. ELIMINAR LOCAL PRIMERO (Esto hace que desaparezca de la lista al instante)
        enfermedadDao.eliminarPorId(id)

        // 2. Intentar eliminar en la API de fondo
        return try {
            apiService.eliminarEnfermedad(id)
            true
        } catch (e: Exception) {
            true // Retornamos true porque ya se borró del celular
        }
    }

    suspend fun obtenerEnfermedades(idUsuario: Int): List<Enfermedad> {
        return enfermedadDao.obtenerPorUsuario(idUsuario)
    }
}
