package com.fernanda.medialert.ui.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernanda.medialert.data.repositories.MedicamentoRepository

class MedicamentoViewModelFactory(private val repository: MedicamentoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicamentoViewModel::class.java)) {
            return MedicamentoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

