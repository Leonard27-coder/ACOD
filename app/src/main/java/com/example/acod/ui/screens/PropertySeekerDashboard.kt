package com.example.acod.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acod.data.Property

@Composable
fun PropertySeekerDashboard(
    userName: String,
    properties: List<Property>,
    onPropertyClick: (Int) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedListingType by remember {
        mutableStateOf("ALL")
    }

    var selectedPayment by remember {
        mutableStateOf("ALL")
    }

    // Search and filter properties.
    val filteredProperties = remember(
        properties,
        searchQuery,
        selectedListingType,
        selectedPayment
    ) {

        properties.filter { property ->

            val matchesSearch =
                searchQuery.isBlank() ||
                property.title.contains(searchQuery.trim(), true) ||
                property.address.contains(searchQuery.trim(), true) ||
                property.description.contains(searchQuery.trim(), true)

            val matchesListingType =
                selectedListingType == "ALL" ||
                property.listingType.equals(
                    selectedListingType,
                    ignoreCase = true
                )

            val matchesPayment =
                selectedPayment == "ALL" ||
                (
                    property.listingType.equals("RENT", true) &&
                    property.paymentDuration.equals(
                        selectedPayment,
                        ignoreCase = true
                    )
                )

            matchesSearch && matchesListingType && matchesPayment
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Dashboard heading
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 14.dp
            )
        ) {

            Text(
                text = "ACOD",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Hello, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Find a home that fits your lifestyle.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Property search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                label = {
                    Text("Search properties")
                },
                placeholder = {
                    Text("Property name, location or description")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                searchQuery = ""
                            }
                        ) {
                            Text("Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Rental or sale filter
            Text(
                text = "What are you looking for?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "ALL" to "All",
                    "RENT" to "For Rent",
                    "SALE" to "For Sale"
                ).forEach { (value, label) ->

                    FilterChip(
                        selected = selectedListingType == value,
                        onClick = {
                            selectedListingType = value
                        },
                        label = {
                            Text(label)
                        }
                    )
                }
            }

            // Payment preference filter
            Text(
                text = "Payment preference",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "ALL" to "All",
                    "Monthly" to "Monthly",
                    "Quarterly" to "Quarterly"
                ).forEach { (value, label) ->

                    FilterChip(
                        selected = selectedPayment == value,
                        onClick = {
                            selectedPayment = value
                        },
                        label = {
                            Text(label)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Available Properties",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${filteredProperties.size} found",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Property results
        when {

            properties.isEmpty() -> {

                EmptyDashboardMessage(
                    message = "No rental or sale properties are available yet."
                )
            }

            filteredProperties.isEmpty() -> {

                EmptyDashboardMessage(
                    message = "No matching properties found. Try changing your search or filters."
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(
                        items = filteredProperties,
                        key = { it.id }
                    ) { property ->

                        SeekerPropertyCard(
                            property = property,
                            onClick = {
                                onPropertyClick(property.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeekerPropertyCard(
    property: Property,
    onClick: () -> Unit
) {

    val isSale = property.listingType.equals(
        "SALE",
        ignoreCase = true
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            // Display uploaded property photograph
            PropertyPhoto(
                property = property,
                height = 190.dp
            )

            Text(
                text = if (isSale) "FOR SALE" else "FOR RENT",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = property.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Location: ${property.address}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = property.description,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (isSale) {
                    "Asking price: ZMW ${property.price}"
                } else {
                    "Monthly rent: ZMW ${property.price}"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (!isSale) {
                Text(
                    text = "Payment: ${property.paymentDuration}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "View property details →",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyDashboardMessage(
    message: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🏠",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}