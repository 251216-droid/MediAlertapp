package com.fernanda.medialert.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernanda.medialert.data.local.entity.Enfermedad
import com.fernanda.medialert.data.repositories.EnfermedadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnfermedadViewModel(private val repository: EnfermedadRepository) : ViewModel() {

    private val _enfermedades = MutableStateFlow<List<Enfermedad>>(emptyList())
    val enfermedades: StateFlow<List<Enfermedad>> = _enfermedades

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarEnfermedades(idUsuario: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = repository.obtenerEnfermedades(idUsuario)
                _enfermedades.value = lista
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun guardarEnfermedad(idUsuario: Int, nombre: String, fecha: String, descripcion: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.agregarEnfermedad(idUsuario, nombre, fecha, descripcion)
                // Recargar la lista después de guardar
                val lista = repository.obtenerEnfermedades(idUsuario)
                _enfermedades.value = lista
                _mensaje.value = "✅ Enfermedad guardada"
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarEnfermedad(
        idEnfermedad: Int, idUsuario: Int,
        nombre: String, fecha: String, descripcion: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.editarEnfermedad(idEnfermedad, idUsuario, nombre, fecha, descripcion)
                val lista = repository.obtenerEnfermedades(idUsuario)
                _enfermedades.value = lista
                _mensaje.value = "✅ Enfermedad actualizada"
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }

    fun eliminarEnfermedad(enfermedadId: Int, idUsuario: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarEnfermedad(enfermedadId)
                val lista = repository.obtenerEnfermedades(idUsuario)
                _enfermedades.value = lista
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.message}"
            }
        }
    }
}

