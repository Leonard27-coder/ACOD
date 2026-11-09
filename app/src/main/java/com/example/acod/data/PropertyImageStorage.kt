package com.example.acod.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.util.UUID

object PropertyImageStorage {

    private const val IMAGE_FOLDER = "property_images"

    // Copy selected gallery images into ACOD's private storage.
    fun saveImages(
        context: Context,
        imageUris: List<Uri>
    ): List<String> {

        val imageDirectory = File(
            context.filesDir,
            IMAGE_FOLDER
        )

        if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
            throw IllegalStateException(
                "Unable to create property image directory."
            )
        }

        val savedFileNames = mutableListOf<String>()

        try {
            imageUris.forEach { uri ->

                val mimeType =
                    context.contentResolver.getType(uri) ?: "image/jpeg"

                require(mimeType.startsWith("image/")) {
                    "Only image files are supported."
                }

                val extension = MimeTypeMap
                    .getSingleton()
                    .getExtensionFromMimeType(mimeType) ?: "jpg"

                val fileName =
                    "property_${UUID.randomUUID()}.$extension"

                val destinationFile = File(
                    imageDirectory,
                    fileName
                )

                val inputStream =
                    context.contentResolver.openInputStream(uri)
                        ?: throw IllegalStateException(
                            "Unable to read selected image."
                        )

                inputStream.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                savedFileNames.add(fileName)
            }

            return savedFileNames

        } catch (exception: Exception) {

            savedFileNames.forEach { fileName ->
                File(imageDirectory, fileName).delete()
            }

            throw exception
        }
    }

    // Retrieve a saved photograph.
    fun getImageFile(
        context: Context,
        fileName: String
    ): File {

        return File(
            File(context.filesDir, IMAGE_FOLDER),
            fileName
        )
    }

    // Delete photographs belonging to a property.
    fun deleteImages(
        context: Context,
        imageFileNames: List<String>
    ) {
        imageFileNames.forEach { fileName ->
            getImageFile(context, fileName).delete()
        }
    }
}