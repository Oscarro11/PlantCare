package plat.plantcare.app.core.photos.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class DeviceImageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalImageStorage {

    private val importDir get() = File(context.filesDir, "plant_imports").apply { mkdirs() }

    private val cameraDir get() = File(context.cacheDir, "photos")
    private val cameraAuthority get() = "${context.packageName}.fileprovider"

    override suspend fun importImage(source: String): String {
        val bytes = readCompressed(source)
        val file = withContext(Dispatchers.IO) {
            val target = File.createTempFile("plant_", ".jpg", importDir)
            try {
                target.writeBytes(bytes)
            } catch (e: IOException) {
                target.delete()
                throw e
            }
            target
        }
        delete(source)
        return Uri.fromFile(file).toString()
    }

    override suspend fun readForUpload(image: String): ByteArray {
        val owned = ownedPath(image)
        return if (owned != null){
            withContext(Dispatchers.IO) { File(owned).readBytes() }
        } else {
            readCompressed(image)
        }
    }

    override suspend fun delete(image: String) {
        withContext(Dispatchers.IO) {
            val owned = ownedPath(image)
            when {
                owned != null -> File(owned).delete()
                isCameraTemp(image) ->
                    runCatching { context.contentResolver.delete(image.toUri(), null, null) }
                else -> Unit
            }
        }
    }

    override suspend fun clearStale() {
        withContext(Dispatchers.IO) {
            val cutoff = System.currentTimeMillis() - STALE_AFTER_MS
            listOf(importDir, cameraDir).forEach { folder ->
                folder.listFiles()
                    ?.filter { it.isFile && it.lastModified() < cutoff }
                    ?.forEach { it.delete() }
            }
        }
    }

    private fun isCameraTemp(image: String) = image.startsWith("content://$cameraAuthority/")

    private fun ownedPath(image: String): String? {
        if (!image.startsWith("file://")) return null
        val path = image.toUri().path ?: return null
        val canonical = runCatching { File(path).canonicalPath }.getOrNull() ?: return null
        return canonical.takeIf { it.startsWith(importDir.canonicalPath + File.separator) }
    }

    private suspend fun readCompressed(image: String): ByteArray = withContext(Dispatchers.IO) {
        val uri = image.toUri()

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("Cannot decode $image")

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE_PX) sample *= 2

        val decoded = open(uri).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw IOException("Cannot decode $image")

        val scale = min(1f, MAX_SIDE_PX / max(decoded.width, decoded.height).toFloat())
        val matrix = Matrix().apply {
            postRotate(exifRotation(uri).toFloat())
            postScale(scale, scale)
        }
        val result = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
        if (result !== decoded) decoded.recycle()

        ByteArrayOutputStream().use { out ->
            result.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            result.recycle()
            out.toByteArray()
        }
    }

    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri) ?: throw IOException("Cannot open $uri")

    private fun exifRotation(uri: Uri): Int =
        try {
            open(uri).use { stream ->
                when (ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
        } catch (_: IOException) {
            0
        }

    private companion object {
        const val MAX_SIDE_PX = 1024
        const val JPEG_QUALITY = 80
        const val STALE_AFTER_MS = 24 * 60 * 60 * 1000L
    }
}