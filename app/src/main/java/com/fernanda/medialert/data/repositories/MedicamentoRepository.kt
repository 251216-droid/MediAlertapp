package com.fernanda.medialert.data.repositories

import android.content.Context
import com.fernanda.medialert.data.local.dao.MedicamentoDao
import com.fernanda.medialert.data.local.dao.ProgramacionDao
import com.fernanda.medialert.data.local.entity.Medicamento
import com.fernanda.medialert.data.local.entity.Programacion
import com.fernanda.medialert.data.remote.*
import com.fernanda.medialert.util.AlarmUtils

class MedicamentoRepository(
    private val context: Context,
    private val medicamentoDao: MedicamentoDao,
    private val programacionDao: ProgramacionDao,
    private val apiService: MedicamentoApiService
) {
    // ── AGREGAR ────────────────────────────────────────────────
    suspend fun agregarMedicamento(
        idUsuario: Int, nombre: String, tipo: String, dosis: String,
        categoria: String, estado: String
    ): MedicamentoResponse? {
        return try {
            val response = apiService.agregarMedicamento(
                MedicamentoRequest(idUsuario, nombre, tipo, dosis, categoria, estado)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                medicamentoDao.insertarMedicamento(
                    Medicamento(body.idMedicamento, idUsuario, nombre, tipo, dosis, categoria, estado)
                )
                body
            } else null
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    // ── SINCRONIZAR DESDE API ──────────────────────────────────
    private suspend fun sincronizarDesdeApi(idUsuario: Int) {
        try {
            val response = apiService.obtenerMedicamentos(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                medicamentoDao.eliminarPorUsuario(idUsuario)
                response.body()!!.forEach { med ->
                    medicamentoDao.insertarMedicamento(
                        Medicamento(
                            idMedicamento = med.idMedicamento,
                            idUsuarioFk = idUsuario,
                            nombreMedicamento = med.nombre_medicamento,
                            tipoPresentacion = med.tipo_presentacion,
                            dosis = med.dosis,
                            categoria = med.categoria,
                            estadoMedicamento = med.estado_medicamento
                        )
                    )
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    // ── OBTENER (sincroniza desde API siempre) ─────────────────
    suspend fun obtenerMedicamentos(idUsuario: Int): List<Medicamento> {
        sincronizarDesdeApi(idUsuario)
        return medicamentoDao.obtenerMedicamentosPorUsuario(idUsuario)
    }

    // ── OBTENER CON PROGRAMACION (para pantalla de editar) ─────
    suspend fun obtenerMedicamentosConProgramacion(idUsuario: Int): List<MedicamentoResponse> {
        return try {
            val response = apiService.obtenerMedicamentos(idUsuario)
            if (response.isSuccessful) response.body() ?: emptyList()
            else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    // ── EDITAR MEDICAMENTO + PROGRAMACION ─────────────────────
    suspend fun editarMedicamentoCompleto(
        idMed: Int, idUser: Int, nom: String, tipo: String, dos: String,
        cat: String, est: String,
        hora: String, frecuencia: Int, duracionDias: Int
    ): Boolean {
        // 1. Actualizar en Room local
        medicamentoDao.actualizarMedicamento(Medicamento(idMed, idUser, nom, tipo, dos, cat, est))

        // 2. Actualizar medicamento en API
        return try {
            apiService.editarMedicamento(idMed, MedicamentoRequest(idUser, nom, tipo, dos, cat, est))
            // 3. Actualizar programación en API
            apiService.actualizarProgramacion(idMed, ProgramacionUpdateRequest(hora, frecuencia, "Todos", duracionDias))
            // 4. Reprogramar alarma con nueva hora y frecuencia
            AlarmUtils.programarAlarma(context, idMed, nom, dos, hora)
            true
        } catch (e: Exception) { e.printStackTrace(); true }
    }

    // ── EDITAR SOLO MEDICAMENTO (sin cambiar horario) ─────────
    suspend fun editarMedicamento(
        idMed: Int, idUser: Int, nom: String, tipo: String, dos: String, cat: String, est: String
    ): Boolean {
        medicamentoDao.actualizarMedicamento(Medicamento(idMed, idUser, nom, tipo, dos, cat, est))
        return try {
            apiService.editarMedicamento(idMed, MedicamentoRequest(idUser, nom, tipo, dos, cat, est)).isSuccessful
        } catch (e: Exception) { true }
    }

    // ── ELIMINAR ───────────────────────────────────────────────
    suspend fun eliminarMedicamento(medicamento: Medicamento): Boolean {
        medicamentoDao.eliminarMedicamento(medicamento)
        return try {
            apiService.eliminarMedicamento(medicamento.idMedicamento).isSuccessful
        } catch (e: Exception) { true }
    }

    // ── AGREGAR PROGRAMACION ───────────────────────────────────
    suspend fun agregarProgramacion(
        idMedicamento: Int, nombre: String, dosis: String,
        hora: String, frecuencia: Int, dias: String, duracionDias: Int = 0
    ) {
        programacionDao.insertarProgramacion(
            Programacion(idMedicamentoFk = idMedicamento, horaPrimeraToma = hora, frecuenciaHoras = frecuencia, diasSemana = dias)
        )
        AlarmUtils.programarAlarma(context, idMedicamento, nombre, dosis, hora)
        try {
            apiService.agregarProgramacion(ProgramacionRequest(idMedicamento, hora, frecuencia, dias, duracionDias))
        } catch (e: Exception) { e.printStackTrace() }
    }
}

