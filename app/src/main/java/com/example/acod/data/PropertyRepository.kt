package com.example.acod.data

import kotlinx.coroutines.flow.Flow

class PropertyRepository(
        private val propertyDao: PropertyDao
) {

    // Retrieve all saved properties
    val allProperties: Flow<List<Property>> =
            propertyDao.getAllProperties()

    // Save a new property
    suspend fun insertProperty(property: Property) {
        propertyDao.insertProperty(property)
    }

    // Retrieve a property using its ID
    suspend fun getPropertyById(id: Int): Property? {
        return propertyDao.getPropertyById(id)
    }

    // Delete a property
    suspend fun deleteProperty(property: Property) {
        propertyDao.deleteProperty(property)
    }
}

