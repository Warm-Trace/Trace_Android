package com.virtuous.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class ImageResizer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    internal fun resizeImage(uri: String, width: Int = 512, height: Int = 512): InputStream {
        // logFileSize(uri)
        val bitmap = decodeBitmap(uri, width, height)
        val orientation = readOrientation(uri)
        val rotated = rotateBitmap(bitmap, orientation)
        return compressBitmap(rotated)
    }

//    private fun logFileSize(uri: String) {
//        val parsedUri = uri.toUri()
//        val afd = context.contentResolver.openAssetFileDescriptor(parsedUri, "r")
//        if (afd != null) {
//            val sizeBytes = afd.length
//            afd.close()
//
//            val kb = sizeBytes / 1024.0
//            Log.d("ImageSize", "original Image Size : ${"%.2f".format(kb)} KB")
//        }
//    }

//
//    private fun logStreamSize(input: InputStream) {
//        val bytes = input.readBytes()
//        val kb = bytes.size / 1024.0
//        Log.d("ImageSize", "Stream size: ${"%.2f".format(kb)} KB")
//
//        val outFile = File(context.cacheDir, "test.jpg")
//        outFile.outputStream().use { it.write(bytes) }
//    }


    private fun decodeBitmap(uri: String, width: Int, height: Int): Bitmap {
        val parsedUri = uri.toUri()

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(parsedUri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false

        return context.contentResolver.openInputStream(parsedUri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw IllegalStateException("Decode 실패")
    }

    private fun readOrientation(uri: String): Int {
        val parsedUri = uri.toUri()
        return context.contentResolver.openInputStream(parsedUri)?.use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.setScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setScale(1f, -1f)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(270f)
                matrix.postScale(-1f, 1f)
            }

            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun compressBitmap(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()

        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.JPEG
        }

        bitmap.compress(format, 100, outputStream)

        // logStreamSize(ByteArrayInputStream(outputStream.toByteArray()))
        return ByteArrayInputStream(outputStream.toByteArray())
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        Log.d("Image Height, Width", "$height, $width")

        var inSampleSize = 1
        while (height / inSampleSize >= reqHeight && width / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
        return inSampleSize
    }
}