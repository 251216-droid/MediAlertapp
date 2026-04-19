package com.fernanda.medialert.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fernanda.medialert.data.local.entity.HistorialToma

@Dao
interface HistorialTomaDao {
    @Insert
    suspend fun registrarToma(historialToma: HistorialToma)

    // Esta consulta es clave para tu Etapa 3: Actualizar si se tomó la medicina o no
    @Query("UPDATE historial_tomas SET estado = :nuevoEstado, fecha_hora_real = :fechaReal WHERE idToma = :idToma")
    suspend fun actualizarEstadoToma(idToma: Int, nuevoEstado: String, fechaReal: String)
}
