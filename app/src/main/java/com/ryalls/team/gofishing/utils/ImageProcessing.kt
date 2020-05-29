package com.ryalls.team.gofishing.utils


import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import java.io.InputStream

object ImageProcessing {

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    @Throws(IOException::class)
    fun rotateImageIfRequired(selectedImage: String): Float {
        val file = File(selectedImage)
        if (file.exists()) {
            try {
                val ei = ExifInterface(file)
                val orientation =
                    ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        9999
                    )
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> return 90.0f
                    ExifInterface.ORIENTATION_ROTATE_180 -> return 180.0f
                    ExifInterface.ORIENTATION_ROTATE_270 -> return 270.0f
                    else -> 0.0f
                }
            } catch (e: IOException) {
                Log.d("EXIF", e.message)
            }
        }
        return 0.0f
    }


    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    @Throws(IOException::class)
    fun rotateImageStreamIfRequired(selectedImage: InputStream?): Float {
        try {
            val ei = ExifInterface(selectedImage!!)
            val orientation =
                ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90.0f
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180.0f
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270.0f
                else -> 0.0f
            }
        } catch (e: IOException) {
            Log.d("EXIF", e.message)
        }
        return 0.0f
    }

}
