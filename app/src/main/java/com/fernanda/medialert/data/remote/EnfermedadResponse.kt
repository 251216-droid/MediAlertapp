package com.fernanda.medialert.data.remote

import com.google.gson.annotations.SerializedName

data class EnfermedadResponse(
    @SerializedName("idEnfermedad")      val idEnfermedad: Int,
    @SerializedName("id_usuario_fk")     val idUsuario: Int,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String,
    @SerializedName("descripcion")       val descripcion: String?,
    @SerializedName("fecha_diagnostico") val fechaDiagnostico: String?
)

// Respuesta del POST /agregar (incluye el ID generado por MySQL)
data class EnfermedadAgregarResponse(
    @SerializedName("idEnfermedad")      val idEnfermedad: Int,
    @SerializedName("id_usuario_fk")     val idUsuario: Int?,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String?,
    @SerializedName("mensaje")           val mensaje: String?
)

data class MensajeResponse(val mensaje: String)

