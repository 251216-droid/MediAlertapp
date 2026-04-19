package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Keep
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

@Keep
data class ActualizarPerfilRequest(
    @SerializedName("nombre_completo") val nombre_completo: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String = ""
)

@Keep
data class FcmTokenRequest(
    @SerializedName("fcm_token") val fcm_token: String
)

