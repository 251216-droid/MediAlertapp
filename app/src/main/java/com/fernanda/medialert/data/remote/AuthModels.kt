package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegistroRequest(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)

@Keep
data class AuthResponse(
    @SerializedName("message") val mensaje: String?,
    @SerializedName("idUsuario") val idUsuario: Int?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("correo") val correo: String?
)

@Keep
data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)

