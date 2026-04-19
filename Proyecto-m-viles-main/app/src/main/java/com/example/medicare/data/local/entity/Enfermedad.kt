package com.example.medicare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "enfermedades")
data class Enfermedad(
    @PrimaryKey(autoGenerate = true)
    val idEnfermedad: Int = 0,

    @ColumnInfo(name = "id_usuario_fk")
    val idUsuarioFk: Int,

    @ColumnInfo(name = "nombre_enfermedad")
    val nombreEnfermedad: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "fecha_diagnostico")
    val fechaDiagnostico: String // Nuevo campo para el diseño de Figma
)
