package com.fernanda.medialert.ui.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernanda.medialert.data.local.entity.Medicamento
import com.fernanda.medialert.data.remote.MedicamentoResponse
import com.fernanda.medialert.data.repositories.MedicamentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicamentoViewModel(private val repository: MedicamentoRepository) : ViewModel() {

    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos

    // Lista con datos de programación incluidos (para edición)
    private val _medicamentosConProg = MutableStateFlow<List<MedicamentoResponse>>(emptyList())
    val medicamentosConProg: StateFlow<List<MedicamentoResponse>> = _medicamentosConProg

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarMedicamentos(idUsuario: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Cargar con programación (para poder precargar datos al editar)
                val conProg = repository.obtenerMedicamentosConProgramacion(idUsuario)
                _medicamentosConProg.value = conProg
                // Cargar lista local sincronizada para la UI
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
        estado: String, hora: String, frecuencia: Int, dias: String, duracionDias: Int = 0
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.agregarMedicamento(idUsuario, nombre, tipo, dosis, categoria, estado)
                if (response != null) {
                    repository.agregarProgramacion(response.idMedicamento, nombre, dosis, hora, frecuencia, dias, duracionDias)
                    _mensaje.value = "✅ Guardado con éxito"
                    cargarMedicamentos(idUsuario)
                } else {
                    _mensaje.value = "❌ Error al guardar"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Editar medicamento + programación completa
    fun editarMedicamentoCompleto(
        idMed: Int, idUser: Int, nom: String, tipo: String, dos: String,
        cat: String, est: String, hora: String, frecuencia: Int, duracionDias: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.editarMedicamentoCompleto(idMed, idUser, nom, tipo, dos, cat, est, hora, frecuencia, duracionDias)
                _mensaje.value = "✅ Actualizado con éxito"
                cargarMedicamentos(idUser)
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

    // Obtener datos de programación de un medicamento por ID
    fun obtenerProgramacionDeMedicamento(idMedicamento: Int): MedicamentoResponse? {
        return _medicamentosConProg.value.find { it.idMedicamento == idMedicamento }
    }
}

