package com.example.acod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acod.data.PropertyDatabase
import com.example.acod.data.PropertyRepository
import com.example.acod.navigation.AcodNavigation
import com.example.acod.ui.PropertyViewModel
import com.example.acod.ui.PropertyViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the Room database
        val database = PropertyDatabase.getDatabase(
            applicationContext
        )

        // 2. Initialize the repository
        val repository = PropertyRepository(
            database.propertyDao()
        )

        // 3. Create the ViewModel factory
        val factory = PropertyViewModelFactory(
            repository
        )

        // 4. Start the ACOD application
        setContent {

            // Connect the ViewModel to Compose
            val propertyViewModel: PropertyViewModel =
                viewModel(factory = factory)

            MaterialTheme {

                Surface(
                    modifier = Modifier
                ) {

                    // Display the navigation system
                    AcodNavigation(
                        viewModel = propertyViewModel
                    )
                }
            }
        }
    }
}
