package com.example.acod.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
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
fun DiscoverHomesScreen(
    userName: String,
    isOwner: Boolean,
    properties: List<Property>,
    onPropertyClick: (Int) -> Unit,
    onAddProperty: () -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    val filteredProperties = remember(
        properties,
        searchQuery,
        selectedFilter
    ) {

        properties.filter { property ->

            val matchesSearch =
                searchQuery.isBlank() ||
                property.title.contains(searchQuery, true) ||
                property.address.contains(searchQuery, true) ||
                property.description.contains(searchQuery, true)

            val matchesType =
                selectedFilter == "All" ||
                property.listingType.equals(
                    selectedFilter,
                    ignoreCase = true
                )

            matchesSearch && matchesType
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Welcome, $userName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Manage and explore your property listings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text("Search your properties")
            },
            placeholder = {
                Text("Search by title or location")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = { searchQuery = "" }
                    ) {
                        Text("Clear")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Listing type",
            fontWeight = FontWeight.SemiBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf("All", "RENT", "SALE").forEach { filter ->

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                    },
                    label = {
                        Text(
                            when (filter) {
                                "RENT" -> "For Rent"
                                "SALE" -> "For Sale"
                                else -> "All"
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${filteredProperties.size} properties found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredProperties.isEmpty()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (properties.isEmpty()) {
                        "No properties have been added yet."
                    } else {
                        "No matching properties found."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(
                    items = filteredProperties,
                    key = { it.id }
                ) { property ->

                    OwnerPropertyCard(
                        property = property,
                        onClick = {
                            onPropertyClick(property.id)
                        }
                    )
                }
            }
        }

        if (isOwner) {

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddProperty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "+ Add New Property",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OwnerPropertyCard(
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

            // Display the actual uploaded photograph.
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
                text = if (isSale) {
                    "Asking price: ZMW ${property.price}"
                } else {
                    "Monthly rent: ZMW ${property.price}"
                },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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