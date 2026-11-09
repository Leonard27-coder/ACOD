package com.example.acod.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acod.data.Property
import com.example.acod.ui.PropertyViewModel
import com.example.acod.ui.screens.AddPropertyScreen
import com.example.acod.ui.screens.DiscoverHomesScreen
import com.example.acod.ui.screens.PropertyDetailsScreen
import com.example.acod.ui.screens.PropertySeekerDashboard
import com.example.acod.ui.screens.RoleSelectionScreen
import com.example.acod.ui.screens.UserRole

private object AcodRoutes {

    const val ROLE_SELECTION = "role_selection"

    const val SEEKER_DASHBOARD = "seeker_dashboard"

    const val OWNER_DASHBOARD = "owner_dashboard"

    const val ADD_PROPERTY = "add_property"

    const val PROPERTY_DETAILS = "property_details/{propertyId}"
}

@Composable
fun AcodNavigation(
    viewModel: PropertyViewModel
) {

    val navController = rememberNavController()

    // Observe properties saved in Room
    val properties by viewModel.allProperties.collectAsState(
        initial = emptyList()
    )

    // Store the selected user's information
    var userName by remember {
        mutableStateOf("")
    }

    var isOwner by remember {
        mutableStateOf(false)
    }

    NavHost(
        navController = navController,
        startDestination = AcodRoutes.ROLE_SELECTION
    ) {

        // SCREEN 1: ROLE SELECTION
        composable(AcodRoutes.ROLE_SELECTION) {

            RoleSelectionScreen(
                onContinue = { name, role ->

                    userName = name

                    isOwner = role == UserRole.PROPERTY_OWNER

                    val destination = if (isOwner) {
                        AcodRoutes.OWNER_DASHBOARD
                    } else {
                        AcodRoutes.SEEKER_DASHBOARD
                    }

                    navController.navigate(destination) {

                        popUpTo(AcodRoutes.ROLE_SELECTION) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // PROPERTY SEEKER DASHBOARD
        composable(AcodRoutes.SEEKER_DASHBOARD) {

            PropertySeekerDashboard(
                userName = userName,
                properties = properties,

                onPropertyClick = { propertyId ->

                    navController.navigate(
                        "property_details/$propertyId"
                    )
                }
            )
        }

        // PROPERTY OWNER DASHBOARD
        composable(AcodRoutes.OWNER_DASHBOARD) {

            DiscoverHomesScreen(
                userName = userName,
                isOwner = true,
                properties = properties,

                onPropertyClick = { propertyId ->

                    navController.navigate(
                        "property_details/$propertyId"
                    )
                },

                onAddProperty = {

                    navController.navigate(
                        AcodRoutes.ADD_PROPERTY
                    )
                }
            )
        }

        // SCREEN 3: PROPERTY DETAILS
        composable(
            route = AcodRoutes.PROPERTY_DETAILS,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val propertyId = backStackEntry.arguments
                ?.getInt("propertyId")

            val selectedProperty: Property? =
                properties.find { it.id == propertyId }

            if (selectedProperty != null) {

                PropertyDetailsScreen(
                    property = selectedProperty,
                    onBack = {
                        navController.popBackStack()
                    }
                )

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Property not found",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Button(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }

        // SCREEN 4: ADD PROPERTY
        composable(AcodRoutes.ADD_PROPERTY) {

            AddPropertyScreen(

                onSaveProperty = { property ->

                    viewModel.addProperty(property)

                    navController.popBackStack()
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
