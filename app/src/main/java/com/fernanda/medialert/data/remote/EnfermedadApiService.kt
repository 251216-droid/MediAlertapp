package com.fernanda.medialert.data.remote

import retrofit2.Response
import retrofit2.http.*

interface EnfermedadApiService {

    @POST("api/enfermedades/agregar")
    suspend fun agregarEnfermedad(@Body request: EnfermedadRequest): Response<EnfermedadAgregarResponse>

    @GET("api/enfermedades/{idUsuario}")
    suspend fun obtenerEnfermedades(@Path("idUsuario") idUsuario: Int): Response<List<EnfermedadResponse>>

    @PUT("api/enfermedades/editar/{id}")
    suspend fun editarEnfermedad(@Path("id") id: Int, @Body request: EnfermedadRequest): Response<Void>

    @DELETE("api/enfermedades/eliminar/{id}")
    suspend fun eliminarEnfermedad(@Path("id") id: Int): Response<MensajeResponse>
}

