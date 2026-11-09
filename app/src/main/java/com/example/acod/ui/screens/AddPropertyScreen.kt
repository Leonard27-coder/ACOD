package com.example.acod.ui.screens

import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.acod.data.Property
import com.example.acod.data.PropertyImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddPropertyScreen(
    onSaveProperty: (Property) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var listingType by remember { mutableStateOf("RENT") }
    var paymentDuration by remember { mutableStateOf("Monthly") }

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    var selectedImages by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Open the phone gallery and select multiple images.
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->

        val combinedImages = (selectedImages + uris).distinct()

        if (combinedImages.size > 6) {
            selectedImages = combinedImages.take(6)
            errorMessage = "You can upload a maximum of six photos."
        } else {
            selectedImages = combinedImages
            errorMessage = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        OutlinedButton(onClick = onBack) {
            Text("Back")
        }

        Text(
            text = "Add New Property",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Provide property information so seekers can make informed decisions."
        )

        // RENTAL OR SALE SELECTION
        Text(
            text = "Listing Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            FilterChip(
                selected = listingType == "RENT",
                onClick = {
                    listingType = "RENT"
                    errorMessage = ""
                },
                label = {
                    Text("For Rent")
                }
            )

            FilterChip(
                selected = listingType == "SALE",
                onClick = {
                    listingType = "SALE"
                    paymentDuration = "Not applicable"
                    errorMessage = ""
                },
                label = {
                    Text("For Sale")
                }
            )
        }

        // PROPERTY TITLE
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = {
                Text("Property title")
            },
            placeholder = {
                Text("e.g. Three-bedroom house")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ADDRESS
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = {
                Text("Property address")
            },
            placeholder = {
                Text("e.g. Chilenje, Lusaka")
            },
            modifier = Modifier.fillMaxWidth()
        )

        // DESCRIPTION
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text("Property description")
            },
            placeholder = {
                Text("Describe rooms, facilities, parking, security and other features.")
            },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        // CONTACT NUMBER
        OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = {
                Text("Owner contact number")
            },
            placeholder = {
                Text("Enter phone number")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // RENT OR SALE PRICE
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = {
                Text(
                    if (listingType == "RENT") {
                        "Monthly rent (ZMW)"
                    } else {
                        "Asking price (ZMW)"
                    }
                )
            },
            placeholder = {
                Text(
                    if (listingType == "RENT") {
                        "e.g. 3500"
                    } else {
                        "e.g. 450000"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // PAYMENT DURATION - RENT ONLY
        if (listingType == "RENT") {

            Text(
                text = "Payment Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = paymentDuration == "Monthly",
                    onClick = {
                        paymentDuration = "Monthly"
                    }
                )

                Text("Monthly")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = paymentDuration == "Quarterly",
                    onClick = {
                        paymentDuration = "Quarterly"
                    }
                )

                Text("Quarterly (3 months in advance)")
            }

        } else {

            Text(
                text = "Sale properties use a one-time asking price.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // PROPERTY COORDINATES
        Text(
            text = "Property Location Coordinates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = {
                Text("Latitude")
            },
            placeholder = {
                Text("e.g. -15.4167")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = {
                Text("Longitude")
            },
            placeholder = {
                Text("e.g. 28.2833")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // PROPERTY PHOTOGRAPHS
        Text(
            text = "Property Photographs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Upload up to six photographs of the property. Clear photographs help seekers understand what you are offering."
        )

        OutlinedButton(
            onClick = {
                imagePicker.launch("image/*")
            },
            enabled = selectedImages.size < 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Photos From Gallery (${selectedImages.size}/6)")
        }

        // Display selected image previews
        if (selectedImages.isNotEmpty()) {

            Text(
                text = "Selected Photographs",
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                selectedImages.forEachIndexed { index, uri ->

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AndroidView(
                            factory = { viewContext ->
                                ImageView(viewContext).apply {
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                }
                            },
                            update = { imageView ->
                                imageView.setImageURI(uri)
                            },
                            modifier = Modifier.size(
                                width = 130.dp,
                                height = 105.dp
                            )
                        )

                        TextButton(
                            onClick = {
                                selectedImages = selectedImages.filterIndexed {
                                    itemIndex, _ -> itemIndex != index
                                }
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        // VALIDATION AND STORAGE ERRORS
        if (errorMessage.isNotBlank()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SAVE PROPERTY
        Button(
            onClick = {

                val enteredPrice = price.toDoubleOrNull()
                val enteredLatitude = latitude.toDoubleOrNull()
                val enteredLongitude = longitude.toDoubleOrNull()

                when {

                    title.isBlank() ||
                    address.isBlank() ||
                    description.isBlank() ||
                    contactNumber.isBlank() ||
                    price.isBlank() ||
                    latitude.isBlank() ||
                    longitude.isBlank() -> {

                        errorMessage = "Please complete all required fields."
                    }

                    enteredPrice == null || !enteredPrice.isFinite() ||
                    enteredPrice <= 0 -> {

                        errorMessage = "Enter a valid property price."
                    }

                    enteredLatitude == null ||
                    !enteredLatitude.isFinite() ||
                    enteredLatitude !in -90.0..90.0 -> {

                        errorMessage = "Enter a valid latitude."
                    }

                    enteredLongitude == null ||
                    !enteredLongitude.isFinite() ||
                    enteredLongitude !in -180.0..180.0 -> {

                        errorMessage = "Enter a valid longitude."
                    }

                    else -> {

                        errorMessage = ""
                        isSaving = true

                        coroutineScope.launch {

                            try {

                                // Copy selected photographs into
                                // ACOD's private storage.
                                val savedImageNames = withContext(
                                    Dispatchers.IO
                                ) {
                                    PropertyImageStorage.saveImages(
                                        context,
                                        selectedImages
                                    )
                                }

                                val property = Property(
                                    id = 0,
                                    title = title.trim(),
                                    address = address.trim(),
                                    description = description.trim(),
                                    contactNumber = contactNumber.trim(),
                                    price = enteredPrice,
                                    paymentDuration = if (
                                        listingType == "RENT"
                                    ) {
                                        paymentDuration
                                    } else {
                                        "Not applicable"
                                    },
                                    latitude = enteredLatitude,
                                    longitude = enteredLongitude,
                                    listingType = listingType,
                                    imagePaths = savedImageNames.joinToString("\n")
                                )

                                onSaveProperty(property)

                            } catch (exception: Exception) {

                                errorMessage = exception.message
                                    ?: "Unable to save property photographs."

                            } finally {

                                isSaving = false
                            }
                        }
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = if (isSaving) {
                    "Saving Property..."
                } else {
                    "Save Property"
                },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}