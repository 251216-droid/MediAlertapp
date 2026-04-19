package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicare.data.local.entity.Programacion

@Dao
interface ProgramacionDao {
    @Insert
    suspend fun insertarProgramacion(programacion: Programacion): Long

    @Query("SELECT * FROM programacion_horarios WHERE id_medicamento_fk = :idMedicamento")
    suspend fun obtenerProgramacionPorMedicamento(idMedicamento: Int): List<Programacion>
}