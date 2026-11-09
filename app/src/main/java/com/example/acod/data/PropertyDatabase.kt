package com.example.acod.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Property::class],
    version = 2,
    exportSchema = false
)
abstract class PropertyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {

        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        // Upgrade database from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {

                // Add property listing type
                database.execSQL(
                    """
                    ALTER TABLE properties
                    ADD COLUMN listingType TEXT NOT NULL DEFAULT 'RENT'
                    """.trimIndent()
                )

                // Add property image filenames
                database.execSQL(
                    """
                    ALTER TABLE properties
                    ADD COLUMN imagePaths TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): PropertyDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertyDatabase::class.java,
                    "acod_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
