package com.example.medicare.ui.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.data.repositories.MedicamentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicamentoViewModel(private val repository: MedicamentoRepository) : ViewModel() {

    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarMedicamentos(idUsuario: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = repository.obtenerMedicamentos(idUsuario)
                _medicamentos.value = lista
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarMedicamentoConHorario(
        idUsuario: Int, nombre: String, tipo: String, dosis: String, categoria: String,
        estado: String, hora: String, frecuencia: Int, dias: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Guardar Medicamento y obtener respuesta
                val response = repository.agregarMedicamento(idUsuario, nombre, tipo, dosis, categoria, estado)
                if (response != null) {
                    // 2. Guardar Programación (IMPORTANTE: Usar nombre y dosis para la alarma)
                    repository.agregarProgramacion(response.idMedicamento, nombre, dosis, hora, frecuencia, dias)
                    _mensaje.value = "Guardado con éxito"
                    cargarMedicamentos(idUsuario)
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarMedicamento(medicamento: Medicamento, idUsuario: Int) {
        viewModelScope.launch {
            repository.eliminarMedicamento(medicamento)
            cargarMedicamentos(idUsuario)
        }
    }

    fun editarMedicamento(idMed: Int, idUser: Int, nom: String, tipo: String, dos: String, cat: String, est: String) {
        viewModelScope.launch {
            repository.editarMedicamento(idMed, idUser, nom, tipo, dos, cat, est)
            cargarMedicamentos(idUser)
        }
    }
}
