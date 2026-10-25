package com.example.util

import android.content.Context
import android.net.Uri
import java.io.File

object ImageUtils {
    /**
     * Copies an image picked via Android Photo Picker to the app's internal files directory
     * so it persists permanently and can be accessed by notification managers and receivers.
     */
    fun savePickedImageLocally(context: Context, sourceUri: Uri): String {
        return try {
            val fileName = "custom_notif_${System.currentTimeMillis()}.jpg"
            val destFile = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            sourceUri.toString()
        }
    }
}
