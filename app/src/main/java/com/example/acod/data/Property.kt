package com.example.acod.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val address: String,

    val description: String,

    val contactNumber: String,

    val price: Double,

    val paymentDuration: String,

    val latitude: Double,

    val longitude: Double,

    // RENT or SALE
    @ColumnInfo(defaultValue = "'RENT'")
    val listingType: String = "RENT",

    // Image filenames stored in app-private storage.
    // Multiple filenames are separated by new lines.
    @ColumnInfo(defaultValue = "''")
    val imagePaths: String = ""
)

// Convert the stored filenames into a list.
fun Property.getImageFileNames(): List<String> {
    return imagePaths
        .split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
