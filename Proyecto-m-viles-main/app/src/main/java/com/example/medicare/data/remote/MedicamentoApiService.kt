package com.example.medicare.data.remote

import retrofit2.Response
import retrofit2.http.*

interface MedicamentoApiService {

    @POST("api/medicamentos/agregar")
    suspend fun agregarMedicamento(@Body request: MedicamentoRequest): Response<MedicamentoResponse>

    @GET("api/medicamentos/usuario/{idUsuario}")
    suspend fun obtenerMedicamentos(@Path("idUsuario") idUsuario: Int): Response<List<MedicamentoResponse>>

    @PUT("api/medicamentos/editar/{id}")
    suspend fun editarMedicamento(@Path("id") id: Int, @Body request: MedicamentoRequest): Response<Unit>

    @DELETE("api/medicamentos/eliminar/{id}")
    suspend fun eliminarMedicamento(@Path("id") id: Int): Response<Unit>

    // Programación de horarios
    @POST("api/programacion/agregar")
    suspend fun agregarProgramacion(@Body request: ProgramacionRequest): Response<Unit>
}

data class MedicamentoRequest(
    val idUsuario: Int,
    val nombre_medicamento: String,
    val tipo_presentacion: String,
    val dosis: String,
    val categoria: String,
    val estado_medicamento: String
)

data class MedicamentoResponse(
    val idMedicamento: Int,
    val id_usuario_fk: Int,
    val nombre_medicamento: String,
    val tipo_presentacion: String,
    val dosis: String,
    val categoria: String,
    val estado_medicamento: String
)

data class ProgramacionRequest(
    val id_medicamento_fk: Int,
    val hora_primera_toma: String,
    val frecuencia_horas: Int,
    val dias_semana: String
)
