package com.example.organizadoracademico.hardware.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ImageSaver(private val context: Context) {

    // Convertida a una función suspendible que devuelve el resultado o lanza una excepción
    suspend fun saveImageToGallery(
        imagePath: String,
        materiaNombre: String
    ): String = withContext(Dispatchers.IO) { // Se asegura de que se ejecuta en un hilo secundario
        suspendCancellableCoroutine { continuation ->
            try {
                val file = File(imagePath)
                if (!file.exists()) {
                    continuation.resumeWithException(Exception("El archivo de imagen no existe."))
                    return@suspendCancellableCoroutine
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
                    continuation.resumeWithException(Exception("No se pudo crear el archivo en la galería."))
                    return@suspendCancellableCoroutine
                }

                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream == null) {
                        continuation.resumeWithException(Exception("No se pudo abrir el stream de salida."))
                        return@suspendCancellableCoroutine
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
                
                continuation.resume(uri.toString()) // Devuelve el resultado en caso de éxito

            } catch (e: Exception) {
                continuation.resumeWithException(Exception("Error al guardar la imagen: ${e.message}"))
            }
        }
    }
}