package com.example.medicare.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Response<AuthResponse>
}
