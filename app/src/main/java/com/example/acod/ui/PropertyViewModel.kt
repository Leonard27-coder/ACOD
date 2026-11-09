package com.example.acod.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.acod.data.Property
import com.example.acod.data.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PropertyViewModel(
        private val repository: PropertyRepository
) : ViewModel() {

    // Observe all saved properties
    val allProperties: Flow<List<Property>> =
    repository.allProperties

    // Save a new property
    fun addProperty(property: Property) {
        viewModelScope.launch {
            repository.insertProperty(property)
        }
    }

    // Delete a property
    fun deleteProperty(property: Property) {
        viewModelScope.launch {
            repository.deleteProperty(property)
        }
    }
}

// Creates the ViewModel with its repository dependency
class PropertyViewModelFactory(
        private val repository: PropertyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
            modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            return PropertyViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

