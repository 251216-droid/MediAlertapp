package com.example.medicare.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface EnfermedadApiService {

    @POST("api/enfermedades/agregar")
    suspend fun agregarEnfermedad(@Body request: EnfermedadRequest): Response<Unit>

    @GET("api/enfermedades/{idUsuario}")
    suspend fun obtenerEnfermedades(@Path("idUsuario") idUsuario: Int): Response<List<EnfermedadResponse>>

    @DELETE("api/enfermedades/eliminar/{id}")
    suspend fun eliminarEnfermedad(@Path("id") id: Int): Response<MensajeResponse>

    @PUT("api/enfermedades/editar/{id}")
    suspend fun editarEnfermedad(@Path("id") id: Int, @Body request: EnfermedadRequest): Response<Unit>
}
