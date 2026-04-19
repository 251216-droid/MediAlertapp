package com.example.medicare.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.data.repositories.EnfermedadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnfermedadViewModel(private val repository: EnfermedadRepository) : ViewModel() {

    private val _enfermedades = MutableStateFlow<List<Enfermedad>>(emptyList())
    val enfermedades: StateFlow<List<Enfermedad>> = _enfermedades

    val listaEnfermedades: List<Enfermedad> get() = _enfermedades.value

    fun cargarEnfermedades(idUsuario: Int) {
        viewModelScope.launch {
            val lista = repository.obtenerEnfermedades(idUsuario)
            _enfermedades.value = lista
        }
    }

    fun guardarEnfermedad(idUsuario: Int, nombre: String, fecha: String, descripcion: String) {
        viewModelScope.launch {
            repository.agregarEnfermedad(idUsuario, nombre, fecha, descripcion)
            cargarEnfermedades(idUsuario)
        }
    }

    fun actualizarEnfermedad(idEnfermedad: Int, idUsuario: Int, nombre: String, fecha: String, descripcion: String) {
        viewModelScope.launch {
            val exito = repository.editarEnfermedad(idEnfermedad, idUsuario, nombre, fecha, descripcion)
            if (exito) {
                cargarEnfermedades(idUsuario)
            }
        }
    }

    fun eliminarEnfermedad(enfermedadId: Int, idUsuario: Int) {
        viewModelScope.launch {
            val exito = repository.eliminarEnfermedad(enfermedadId)
            if (exito) {
                cargarEnfermedades(idUsuario)
            }
        }
    }
}
