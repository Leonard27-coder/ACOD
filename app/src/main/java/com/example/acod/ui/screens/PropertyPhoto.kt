package com.example.acod.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.acod.data.Property
import com.example.acod.data.PropertyImageStorage
import com.example.acod.data.getImageFileNames

@Composable
fun PropertyPhoto(
    property: Property,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {

    val context = LocalContext.current

    val imageNames = property.getImageFileNames()

    val firstImage = imageNames.firstOrNull()?.let {
        PropertyImageStorage.getImageFile(context, it)
    }?.takeIf {
        it.isFile
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        if (firstImage != null) {

            // Display the actual uploaded property photograph.
            AsyncImage(
                model = firstImage,
                contentDescription = "Photo of ${property.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Show the number of photographs uploaded.
            if (imageNames.size > 1) {

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Text(
                        text = "${imageNames.size} photos",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

        } else {

            // Fallback when no photograph exists.
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🏠",
                    style = MaterialTheme.typography.displayLarge
                )

                Text(
                    text = "No property photo",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}