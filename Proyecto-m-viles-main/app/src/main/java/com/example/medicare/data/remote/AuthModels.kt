package com.example.medicare.data.remote

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)

data class AuthResponse(
    @SerializedName("message") val mensaje: String?,
    @SerializedName("idUsuario") val idUsuario: Int?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("correo") val correo: String?
)

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)
