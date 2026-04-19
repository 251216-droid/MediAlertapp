package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

@Keep
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

@Keep
data class ProximaDosisResponse(
    @SerializedName("idProgramacion") val idProgramacion: Int,
    @SerializedName("nombre_medicamento") val nombre_medicamento: String,
    @SerializedName("tipo_presentacion") val tipo_presentacion: String,
    @SerializedName("dosis") val dosis: String,
    @SerializedName("hora_primera_toma") val hora_primera_toma: String,
    @SerializedName("frecuencia_horas") val frecuencia_horas: Int,
    @SerializedName("proxima_toma") val proxima_toma: String = "",
    @SerializedName("proxima_toma_timestamp") val proxima_toma_timestamp: Long = 0L
)

@Keep
data class TomaRequest(
    @SerializedName("id_programacion_fk") val id_programacion_fk: Int,
    @SerializedName("fecha_hora_programada") val fecha_hora_programada: String,
    @SerializedName("estado") val estado: String
)

@Keep
data class HistorialResponse(
    @SerializedName("idToma") val idToma: Int,
    @SerializedName("nombre_medicamento") val nombre_medicamento: String,
    @SerializedName("fecha_hora_real") val fecha_hora_real: String,
    @SerializedName("estado") val estado: String
)

