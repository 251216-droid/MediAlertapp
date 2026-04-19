package com.fernanda.medialert.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

interface HistorialApiService {
    @GET("api/historial/proximas/{idUsuario}")
    suspend fun obtenerProximasDosis(@Path("idUsuario") idUsuario: Int): Response<List<ProximaDosisResponse>>

    @POST("api/historial/tomar")
    suspend fun registrarToma(@Body request: TomaRequest): Response<Void>

    @GET("api/historial/usuario/{idUsuario}")
    suspend fun obtenerHistorial(@Path("idUsuario") idUsuario: Int): Response<List<HistorialResponse>>

    @DELETE("api/historial/eliminar/{idToma}")
    suspend fun eliminarToma(@Path("idToma") idToma: Int): Response<Void>

    @DELETE("api/historial/limpiar/{idUsuario}")
    suspend fun limpiarHistorial(@Path("idUsuario") idUsuario: Int): Response<Void>
}

data class ProximaDosisResponse(
    val idProgramacion: Int,
    val nombre_medicamento: String,
    val tipo_presentacion: String,
    val dosis: String,
    val hora_primera_toma: String,
    val frecuencia_horas: Int,
    val proxima_toma: String = "",        // HH:MM calculado por la API
    val proxima_toma_timestamp: Long = 0L
)

data class TomaRequest(
    val id_programacion_fk: Int,
    val fecha_hora_programada: String,
    val estado: String
)

data class HistorialResponse(
    val idToma: Int,
    val nombre_medicamento: String,
    val fecha_hora_real: String,
    val estado: String
)

