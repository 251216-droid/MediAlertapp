package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EnfermedadResponse(
    @SerializedName("idEnfermedad")      val idEnfermedad: Int,
    @SerializedName("id_usuario_fk")     val idUsuario: Int,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String,
    @SerializedName("descripcion")       val descripcion: String?,
    @SerializedName("fecha_diagnostico") val fechaDiagnostico: String?
)

// Respuesta del POST /agregar (incluye el ID generado por MySQL)
@Keep
data class EnfermedadAgregarResponse(
    @SerializedName("idEnfermedad")      val idEnfermedad: Int,
    @SerializedName("id_usuario_fk")     val idUsuario: Int?,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?,
    @SerializedName("mensaje")           val mensaje: String?
)

@Keep
data class MensajeResponse(
    @SerializedName("mensaje") val mensaje: String
)

