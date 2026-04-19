package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

@Keep
interface MedicamentoApiService {

    @POST("api/medicamentos/agregar")
    suspend fun agregarMedicamento(@Body request: MedicamentoRequest): Response<MedicamentoResponse>

    @GET("api/medicamentos/usuario/{idUsuario}")
    suspend fun obtenerMedicamentos(@Path("idUsuario") idUsuario: Int): Response<List<MedicamentoResponse>>

    @PUT("api/medicamentos/editar/{id}")
    suspend fun editarMedicamento(@Path("id") id: Int, @Body request: MedicamentoRequest): Response<Void>

    @PUT("api/medicamentos/programacion/{idMedicamento}")
    suspend fun actualizarProgramacion(@Path("idMedicamento") idMedicamento: Int, @Body request: ProgramacionUpdateRequest): Response<Void>

    @DELETE("api/medicamentos/eliminar/{id}")
    suspend fun eliminarMedicamento(@Path("id") id: Int): Response<Void>

    @POST("api/programacion/agregar")
    suspend fun agregarProgramacion(@Body request: ProgramacionRequest): Response<Void>
}

@Keep
data class MedicamentoRequest(
    @SerializedName("idUsuario") val idUsuario: Int,
    @SerializedName("nombre_medicamento") val nombre_medicamento: String,
    @SerializedName("tipo_presentacion") val tipo_presentacion: String,
    @SerializedName("dosis") val dosis: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("estado_medicamento") val estado_medicamento: String
)

@Keep
data class MedicamentoResponse(
    @SerializedName("idMedicamento") val idMedicamento: Int,
    @SerializedName("id_usuario_fk") val id_usuario_fk: Int,
    @SerializedName("nombre_medicamento") val nombre_medicamento: String,
    @SerializedName("tipo_presentacion") val tipo_presentacion: String,
    @SerializedName("dosis") val dosis: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("estado_medicamento") val estado_medicamento: String,
    // Programacion (puede ser null si no tiene aún)
    @SerializedName("idProgramacion") val idProgramacion: Int? = null,
    @SerializedName("hora_primera_toma") val hora_primera_toma: String? = null,
    @SerializedName("frecuencia_horas") val frecuencia_horas: Int? = null
)

@Keep
data class ProgramacionRequest(
    @SerializedName("id_medicamento_fk") val id_medicamento_fk: Int,
    @SerializedName("hora_primera_toma") val hora_primera_toma: String,
    @SerializedName("frecuencia_horas") val frecuencia_horas: Int,
    @SerializedName("dias_semana") val dias_semana: String,
    @SerializedName("duracion_dias") val duracion_dias: Int = 0
)

@Keep
data class ProgramacionUpdateRequest(
    @SerializedName("hora_primera_toma") val hora_primera_toma: String,
    @SerializedName("frecuencia_horas") val frecuencia_horas: Int,
    @SerializedName("dias_semana") val dias_semana: String = "Todos",
    @SerializedName("duracion_dias") val duracion_dias: Int = 0
)

