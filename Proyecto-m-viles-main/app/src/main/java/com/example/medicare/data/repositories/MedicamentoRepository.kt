package com.example.medicare.data.repositories

import android.content.Context
import com.example.medicare.data.local.dao.MedicamentoDao
import com.example.medicare.data.local.dao.ProgramacionDao
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.data.local.entity.Programacion
import com.example.medicare.data.remote.*
import com.example.medicare.util.AlarmUtils

class MedicamentoRepository(
    private val context: Context,
    private val medicamentoDao: MedicamentoDao,
    private val programacionDao: ProgramacionDao,
    private val apiService: MedicamentoApiService
) {
    suspend fun agregarMedicamento(
        idUsuario: Int, nombre: String, tipo: String, dosis: String, categoria: String, estado: String
    ): MedicamentoResponse? {
        return try {
            val request = MedicamentoRequest(idUsuario, nombre, tipo, dosis, categoria, estado)
            val response = apiService.agregarMedicamento(request)

            if (response.isSuccessful && response.body() != null) {
                val medResponse = response.body()!!
                // Guardamos en Room con el ID real que nos dio MySQL
                val medLocal = Medicamento(
                    idMedicamento = medResponse.idMedicamento,
                    idUsuarioFk = idUsuario,
                    nombreMedicamento = nombre,
                    tipoPresentacion = tipo,
                    dosis = dosis,
                    categoria = categoria,
                    estadoMedicamento = estado
                )
                medicamentoDao.insertarMedicamento(medLocal)
                medResponse
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerMedicamentos(idUsuario: Int): List<Medicamento> {
        return medicamentoDao.obtenerMedicamentosPorUsuario(idUsuario)
    }

    suspend fun editarMedicamento(idMed: Int, idUser: Int, nom: String, tipo: String, dos: String, cat: String, est: String): Boolean {
        val medLocal = Medicamento(idMed, idUser, nom, tipo, dos, cat, est)
        medicamentoDao.actualizarMedicamento(medLocal)
        return try {
            val request = MedicamentoRequest(idUser, nom, tipo, dos, cat, est)
            apiService.editarMedicamento(idMed, request).isSuccessful
        } catch (e: Exception) { true }
    }

    suspend fun eliminarMedicamento(medicamento: Medicamento): Boolean {
        medicamentoDao.eliminarMedicamento(medicamento)
        return try {
            apiService.eliminarMedicamento(medicamento.idMedicamento).isSuccessful
        } catch (e: Exception) { true }
    }

    suspend fun agregarProgramacion(idMedicamento: Int, nombre: String, dosis: String, hora: String, frecuencia: Int, dias: String) {
        val progLocal = Programacion(idMedicamentoFk = idMedicamento, horaPrimeraToma = hora, frecuenciaHoras = frecuencia, diasSemana = dias)
        programacionDao.insertarProgramacion(progLocal)

        // Programar alarma física
        AlarmUtils.programarAlarma(context, idMedicamento, nombre, dosis, hora)

        try {
            apiService.agregarProgramacion(ProgramacionRequest(idMedicamento, hora, frecuencia, dias))
        } catch (e: Exception) { }
    }
}
