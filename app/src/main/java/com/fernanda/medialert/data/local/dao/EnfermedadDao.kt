package com.fernanda.medialert.data.local.dao

import androidx.room.*
import com.fernanda.medialert.data.local.entity.Enfermedad

@Dao
interface EnfermedadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enfermedad: Enfermedad)

    @Query("SELECT * FROM enfermedades WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerPorUsuario(idUsuario: Int): List<Enfermedad>

    @Update
    suspend fun actualizar(enfermedad: Enfermedad)

    @Delete
    suspend fun eliminar(enfermedad: Enfermedad)

    @Query("DELETE FROM enfermedades WHERE idEnfermedad = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("DELETE FROM enfermedades WHERE id_usuario_fk = :idUsuario")
    suspend fun eliminarPorUsuario(idUsuario: Int)
}

