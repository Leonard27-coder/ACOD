package com.example.acod.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import coil.compose.AsyncImage
import com.example.acod.data.Property
import com.example.acod.data.PropertyImageStorage
import com.example.acod.data.getImageFileNames

@Composable
fun PropertyDetailsScreen(
    property: Property,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val isSale = property.listingType.equals(
        "SALE",
        ignoreCase = true
    )

    // Retrieve all photographs belonging to this property.
    val imageFiles = property.getImageFileNames().mapNotNull { fileName ->

        PropertyImageStorage.getImageFile(
            context,
            fileName
        ).takeIf { it.isFile }
    }

    val advanceAmount = property.price *
        if (property.paymentDuration.equals("Quarterly", true)) {
            3
        } else {
            1
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        OutlinedButton(
            onClick = onBack
        ) {
            Text("Back to properties")
        }

        Text(
            text = if (isSale) "PROPERTY FOR SALE" else "PROPERTY FOR RENT",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = property.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Location: ${property.address}",
            style = MaterialTheme.typography.bodyLarge
        )

        // PROPERTY PHOTO GALLERY
        Text(
            text = "Property Photographs (${imageFiles.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (imageFiles.isNotEmpty()) {

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(imageFiles) { imageFile ->

                    AsyncImage(
                        model = imageFile,
                        contentDescription = "Photograph of ${property.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(
                                width = 290.dp,
                                height = 210.dp
                            )
                            .clip(
                                RoundedCornerShape(16.dp)
                            )
                    )
                }
            }

            Text(
                text = "Swipe horizontally to view more photographs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No property photographs available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // PROPERTY DESCRIPTION
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Property Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // RENTAL OR SALE INFORMATION
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = if (isSale) {
                        "Sale Information"
                    } else {
                        "Rental Information"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                DetailRow(
                    label = if (isSale) {
                        "Asking price"
                    } else {
                        "Monthly rent"
                    },
                    value = "ZMW ${property.price}"
                )

                if (!isSale) {

                    DetailRow(
                        label = "Payment duration",
                        value = property.paymentDuration
                    )

                    DetailRow(
                        label = "Advance amount",
                        value = "ZMW $advanceAmount"
                    )

                } else {

                    Text(
                        text = "One-time asking price. Contact the owner for purchase arrangements."
                    )
                }
            }
        }

        // OWNER CONTACT
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Owner Contact",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = property.contactNumber,
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = {

                        val dialIntent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:${property.contactNumber}")
                        )

                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("Contact Property Owner")
                }
            }
        }

        // PROPERTY LOCATION
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Property Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                DetailRow(
                    label = "Latitude",
                    value = property.latitude.toString()
                )

                DetailRow(
                    label = "Longitude",
                    value = property.longitude.toString()
                )

                Button(
                    onClick = {

                        val mapUri = Uri.parse(
                            "geo:${property.latitude},${property.longitude}" +
                                "?q=${property.latitude},${property.longitude}" +
                                "(${Uri.encode(property.title)})"
                        )

                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            mapUri
                        )

                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("View Location on Map")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}