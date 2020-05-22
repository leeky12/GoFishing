package com.ryalls.team.gofishing.utils

import androidx.exifinterface.media.ExifInterface

import java.io.IOException

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
        val ei = ExifInterface(selectedImage)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90.0f
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180.0f
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270.0f
            else -> 0.0f
        }
    }

}