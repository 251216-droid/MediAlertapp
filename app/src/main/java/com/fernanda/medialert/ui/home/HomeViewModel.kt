package com.fernanda.medialert.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernanda.medialert.data.remote.HistorialResponse
import com.fernanda.medialert.data.remote.ProximaDosisResponse
import com.fernanda.medialert.data.repositories.HistorialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HistorialRepository
) : ViewModel() {

    private val _proximasDoses = MutableStateFlow<List<ProximaDosisResponse>>(emptyList())
    val proximasDoses: StateFlow<List<ProximaDosisResponse>> = _proximasDoses

    private val _historial = MutableStateFlow<List<HistorialResponse>>(emptyList())
    val historial: StateFlow<List<HistorialResponse>> = _historial

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarDatosHome(idUsuario: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            actualizarDatos(idUsuario)
            _isLoading.value = false
        }
    }

    private suspend fun actualizarDatos(idUsuario: Int) {
        try {
            val doses = repository.obtenerProximasDosis(idUsuario)
            if (doses != null) _proximasDoses.value = doses

            val data = repository.obtenerHistorial(idUsuario)
            if (data != null) _historial.value = data
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── TOMADO o OMITIDO desde la pantalla de inicio ──────────────────────────
    // Las notificaciones ahora vienen de FCM (no del AlarmManager local).
    // Solo necesitamos: registrar en MySQL + quitar la tarjeta de la UI.
    fun registrarTomaManual(
        idProgramacion: Int,
        horaProgramada: String,
        estado: String,
        idUsuario: Int
    ) {
        viewModelScope.launch {
            // Quitar de la UI inmediatamente (respuesta instantánea)
            if (estado == "Tomado" || estado == "No Tomado") {
                _proximasDoses.value = _proximasDoses.value.filter {
                    it.idProgramacion != idProgramacion
                }
            }
            // Guardar en MySQL
            val exito = repository.registrarToma(idProgramacion, horaProgramada, estado)
            // Recargar para mostrar la siguiente toma calculada por la API
            if (exito) actualizarDatos(idUsuario)
        }
    }

    // ── ELIMINAR registro del historial ───────────────────────────────────────
    fun eliminarRegistroHistorial(idToma: Int, idUsuario: Int) {
        viewModelScope.launch {
            _historial.value = _historial.value.filter { it.idToma != idToma }
            repository.eliminarToma(idToma)
            val data = repository.obtenerHistorial(idUsuario)
            if (data != null) _historial.value = data
        }
    }

    // ── LIMPIAR todo el historial ─────────────────────────────────────────────
    fun limpiarTodoElHistorial(idUsuario: Int) {
        viewModelScope.launch {
            _historial.value = emptyList()
            repository.limpiarHistorial(idUsuario)
        }
    }
}
