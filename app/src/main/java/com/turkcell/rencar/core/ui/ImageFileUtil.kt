package com.turkcell.rencar.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Bir Bitmap'i cache dizinine jpg olarak yazar ve gerçek dosya yolunu döner.
 * Multipart yükleme (License/Handover/Return foto akışları) File nesnesi gerektirdiği için kullanılır.
 */
fun Bitmap.toLocalFile(context: Context, prefix: String): File? {
    return runCatching {
        val file = File.createTempFile(prefix, ".jpg", context.cacheDir)
        ByteArrayOutputStream().use { stream ->
            compress(Bitmap.CompressFormat.JPEG, 90, stream)
            FileOutputStream(file).use { output -> output.write(stream.toByteArray()) }
        }
        file
    }.getOrNull()
}

/**
 * Galeriden seçilen bir Uri'yi decode edip cache dizinine jpg olarak yazar.
 */
fun Uri.toLocalFile(context: Context, prefix: String): File? {
    return runCatching {
        val input = context.contentResolver.openInputStream(this) ?: return null
        val bitmap = input.use { BitmapFactory.decodeStream(it) } ?: return null
        bitmap.toLocalFile(context, prefix)
    }.getOrNull()
}
