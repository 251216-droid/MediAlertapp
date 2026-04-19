package com.fernanda.medialert.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fernanda.medialert.data.repositories.EnfermedadRepository

class EnfermedadViewModelFactory(private val repository: EnfermedadRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnfermedadViewModel::class.java)) {
            return EnfermedadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

