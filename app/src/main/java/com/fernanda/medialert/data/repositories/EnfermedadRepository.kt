package com.fernanda.medialert.data.repositories

import android.util.Log
import com.fernanda.medialert.data.local.dao.EnfermedadDao
import com.fernanda.medialert.data.local.entity.Enfermedad
import com.fernanda.medialert.data.remote.EnfermedadApiService
import com.fernanda.medialert.data.remote.EnfermedadRequest

class EnfermedadRepository(
    private val enfermedadDao: EnfermedadDao,
    private val apiService: EnfermedadApiService
) {
    companion object { private const val TAG = "EnfermedadRepo" }

    // ── AGREGAR ────────────────────────────────────────────────
    suspend fun agregarEnfermedad(
        idUsuario: Int, nombre: String, fecha: String, descripcion: String
    ) {
        Log.d(TAG, "▶ agregarEnfermedad: idUsuario=$idUsuario nombre='$nombre' fecha='$fecha'")

        val request = EnfermedadRequest(
            idUsuario        = idUsuario,
            nombreEnfermedad = nombre,
            fechaDiagnostico = fecha,
            descripcion      = descripcion
        )

        try {
            val response = apiService.agregarEnfermedad(request)
            Log.d(TAG, "API code=${response.code()} body=${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d(TAG, "✅ Guardado en MySQL con ID=${body.idEnfermedad}")
                // Insertar directamente en Room con el ID real de MySQL
                enfermedadDao.insert(
                    Enfermedad(
                        idEnfermedad     = body.idEnfermedad,
                        idUsuarioFk      = idUsuario,
                        nombreEnfermedad = nombre,
                        descripcion      = descripcion,
                        fechaDiagnostico = fecha
                    )
                )
                return
            } else {
                val errorMsg = response.errorBody()?.string() ?: "sin detalle"
                Log.e(TAG, "❌ Error API ${response.code()}: $errorMsg")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción de red: ${e.message}")
        }

        // Fallback: guardar localmente con ID temporal (0 = autoincrement Room)
        Log.w(TAG, "⚠ Guardando solo en Room (sin conexión)")
        enfermedadDao.insert(
            Enfermedad(
                idUsuarioFk      = idUsuario,
                nombreEnfermedad = nombre,
                descripcion      = descripcion,
                fechaDiagnostico = fecha
            )
        )
    }

    // ── SINCRONIZAR DESDE API ──────────────────────────────────
    private suspend fun sincronizarDesdeApi(idUsuario: Int) {
        try {
            val response = apiService.obtenerEnfermedades(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                val lista = response.body()!!
                Log.d(TAG, "Sync: ${lista.size} enfermedades desde API")
                enfermedadDao.eliminarPorUsuario(idUsuario)
                lista.forEach { enf ->
                    enfermedadDao.insert(
                        Enfermedad(
                            idEnfermedad     = enf.idEnfermedad,
                            idUsuarioFk      = idUsuario,
                            nombreEnfermedad = enf.nombreEnfermedad,
                            descripcion      = enf.descripcion ?: "",
                            fechaDiagnostico = enf.fechaDiagnostico ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sync: ${e.message}")
        }
    }

    // ── EDITAR ─────────────────────────────────────────────────
    suspend fun editarEnfermedad(
        idEnfermedad: Int, idUsuario: Int,
        nombre: String, fecha: String, descripcion: String
    ): Boolean {
        // Actualizar localmente primero (respuesta inmediata en UI)
        enfermedadDao.actualizar(
            Enfermedad(
                idEnfermedad     = idEnfermedad,
                idUsuarioFk      = idUsuario,
                nombreEnfermedad = nombre,
                descripcion      = descripcion,
                fechaDiagnostico = fecha
            )
        )
        return try {
            val response = apiService.editarEnfermedad(
                idEnfermedad,
                EnfermedadRequest(idUsuario, nombre, fecha, descripcion)
            )
            Log.d(TAG, "Editar API: ${response.code()}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error editar: ${e.message}")
            true // Ya actualizó localmente
        }
    }

    // ── ELIMINAR ───────────────────────────────────────────────
    suspend fun eliminarEnfermedad(id: Int): Boolean {
        enfermedadDao.eliminarPorId(id)
        return try {
            apiService.eliminarEnfermedad(id)
            Log.d(TAG, "Eliminada ID=$id de MySQL")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminar: ${e.message}")
            true
        }
    }

    // ── OBTENER ────────────────────────────────────────────────
    // Primero sincroniza desde API, luego lee Room
    suspend fun obtenerEnfermedades(idUsuario: Int): List<Enfermedad> {
        sincronizarDesdeApi(idUsuario)
        return enfermedadDao.obtenerPorUsuario(idUsuario)
    }
}

