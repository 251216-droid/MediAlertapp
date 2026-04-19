package com.example.medicare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.remote.HistorialResponse
import com.example.medicare.data.remote.ProximaDosisResponse
import com.example.medicare.data.repositories.HistorialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val repository: HistorialRepository) : ViewModel() {

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
        val doses = repository.obtenerProximasDosis(idUsuario)
        if (doses != null) _proximasDoses.value = doses
        
        val data = repository.obtenerHistorial(idUsuario)
        if (data != null) _historial.value = data
    }

    fun registrarTomaManual(idProgramacion: Int, horaProgramada: String, estado: String, idUsuario: Int) {
        viewModelScope.launch {
            val exito = repository.registrarToma(idProgramacion, horaProgramada, estado)
            if (exito) {
                actualizarDatos(idUsuario)
            }
        }
    }

    fun eliminarRegistroHistorial(idToma: Int, idUsuario: Int) {
        viewModelScope.launch {
            val exito = repository.eliminarToma(idToma)
            if (exito) {
                actualizarDatos(idUsuario)
            }
        }
    }

    fun limpiarTodoElHistorial(idUsuario: Int) {
        viewModelScope.launch {
            val exito = repository.limpiarHistorial(idUsuario)
            if (exito) {
                actualizarDatos(idUsuario)
            }
        }
    }
}
