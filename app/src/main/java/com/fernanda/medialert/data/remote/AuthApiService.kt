package com.fernanda.medialert.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {

    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Response<AuthResponse>

    @PUT("api/auth/actualizar/{id}")
    suspend fun actualizarPerfil(@Path("id") id: Int, @Body request: ActualizarPerfilRequest): Response<AuthResponse>

    @PUT("api/auth/fcm-token/{id}")
    suspend fun actualizarFcmToken(@Path("id") id: Int, @Body request: FcmTokenRequest): Response<AuthResponse>
}

data class ActualizarPerfilRequest(
    val nombre_completo: String,
    val correo: String,
    val password: String = ""
)

data class FcmTokenRequest(
    val fcm_token: String
)

