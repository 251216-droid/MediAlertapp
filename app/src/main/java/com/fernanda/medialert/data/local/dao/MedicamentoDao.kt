package com.fernanda.medialert.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fernanda.medialert.data.local.entity.Medicamento

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMedicamento(medicamento: Medicamento): Long

    @Query("SELECT * FROM medicamentos WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerMedicamentosPorUsuario(idUsuario: Int): List<Medicamento>

    @Update
    suspend fun actualizarMedicamento(medicamento: Medicamento)

    @Delete
    suspend fun eliminarMedicamento(medicamento: Medicamento)

    @Query("DELETE FROM medicamentos WHERE id_usuario_fk = :idUsuario")
    suspend fun eliminarPorUsuario(idUsuario: Int)
}

