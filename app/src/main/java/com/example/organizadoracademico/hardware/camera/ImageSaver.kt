package com.example.organizadoracademico.hardware.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import java.io.File

class ImageSaver(private val context: Context) {

    fun saveImageToGallery(
        imagePath: String,
        materiaNombre: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val file = File(imagePath)
            if (!file.exists()) {
                onError("El archivo de imagen no existe.")
                return
            }

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$materiaNombre")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (uri == null) {
                onError("No se pudo crear el archivo en la galería.")
                return
            }

            resolver.openOutputStream(uri).use { outputStream ->
                if (outputStream == null) {
                    onError("No se pudo abrir el stream de salida.")
                    return
                }
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            // Eliminar el archivo temporal
            file.delete()

            onSuccess(uri.toString())

        } catch (e: Exception) {
            onError("Error al guardar la imagen: ${e.message}")
        }
    }
}