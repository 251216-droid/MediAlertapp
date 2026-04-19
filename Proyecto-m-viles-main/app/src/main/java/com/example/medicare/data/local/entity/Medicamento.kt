package com.example.medicare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "medicamentos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["idUsuario"],
            childColumns = ["id_usuario_fk"], // <--- ¡AQUÍ ESTÁ EL CAMBIO!
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val idMedicamento: Int = 0,

    @ColumnInfo(name = "id_usuario_fk")
    val idUsuarioFk: Int,

    @ColumnInfo(name = "nombre_medicamento")
    val nombreMedicamento: String,

    @ColumnInfo(name = "tipo_presentacion")
    val tipoPresentacion: String, // Ej: Pastilla, Jarabe

    @ColumnInfo(name = "dosis")
    val dosis: String, // Ej: 500mg

    // --- NUEVOS CAMPOS DE REVISIÓN ---
    @ColumnInfo(name = "categoria")
    val categoria: String, // "Pediátrico", "Infantil", "Adulto"

    @ColumnInfo(name = "estado_medicamento")
    val estadoMedicamento: String // "Activo", "Suspendido", "Terminado"
)