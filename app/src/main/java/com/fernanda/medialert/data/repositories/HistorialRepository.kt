package com.fernanda.medialert.data.repositories

import com.fernanda.medialert.data.remote.HistorialApiService
import com.fernanda.medialert.data.remote.ProximaDosisResponse
import com.fernanda.medialert.data.remote.HistorialResponse
import com.fernanda.medialert.data.remote.TomaRequest

class HistorialRepository(private val apiService: HistorialApiService) {

    suspend fun obtenerProximasDosis(idUsuario: Int): List<ProximaDosisResponse>? {
        return try {
            val response = apiService.obtenerProximasDosis(idUsuario)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerHistorial(idUsuario: Int): List<HistorialResponse>? {
        return try {
            val response = apiService.obtenerHistorial(idUsuario)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registrarToma(
        idProgramacion: Int,
        fechaProgramada: String,
        fechaProgramadaDt: String?,
        estado: String
    ): Boolean {
        return try {
            val request = TomaRequest(
                id_programacion_fk = idProgramacion,
                fecha_hora_programada = fechaProgramada,
                fecha_programada_dt = fechaProgramadaDt,
                estado = estado
            )
            val response = apiService.registrarToma(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarToma(idToma: Int): Boolean {
        return try {
            val response = apiService.eliminarToma(idToma)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun limpiarHistorial(idUsuario: Int): Boolean {
        return try {
            val response = apiService.limpiarHistorial(idUsuario)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

