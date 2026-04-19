package com.fernanda.medialert.data.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EnfermedadRequest(
    @SerializedName("id_usuario_fk")   val idUsuario: Int,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String,
    @SerializedName("fecha_diagnostico") val fechaDiagnostico: String,
    @SerializedName("descripcion")     val descripcion: String
)

