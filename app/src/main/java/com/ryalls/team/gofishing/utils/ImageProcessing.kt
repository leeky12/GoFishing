package com.ryalls.team.gofishing.utils


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException

object ImageProcessing {

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

    fun decodeBitmap(pathName: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        Log.d("BitmapMetrics", "Original Bitmap " + options.outHeight + " " + options.outWidth)
        val wRatioinv = options.outWidth.toFloat() / maxWidth
        val hRatioinv =
            options.outHeight.toFloat() / maxHeight // Working with inverse ratios is more comfortable
        val finalW: Int
        val finalH: Int
        val minRatioinv /* = max{Ratio_inv} */: Int
        if (wRatioinv > hRatioinv) {
            minRatioinv = wRatioinv.toInt()
            finalW = maxWidth
            finalH = Math.round(options.outHeight / wRatioinv)
        } else {
            minRatioinv = hRatioinv.toInt()
            finalH = maxHeight
            finalW = Math.round(options.outWidth / hRatioinv)
        }
        options.inSampleSize =
            pow2Ceil(minRatioinv) // pow2Ceil: A utility function that comes later
        //  Raw height and width of image
        options.inJustDecodeBounds = false // Decode bitmap with inSampleSize set
        return Bitmap.createScaledBitmap(
            BitmapFactory.decodeFile(pathName, options),
            finalW, finalH, true
        )
    }

    /**
     * @return the largest power of 2 that is smaller than or equal to number.
     * WARNING: return {0b1000000...000} for ZERO input.
     */
    private fun pow2Ceil(number: Int): Int {
        return 1 shl -(Integer.numberOfLeadingZeros(number) + 1) // is equivalent to:
        // return Integer.rotateRight(1, Integer.numberOfLeadingZeros(number) + 1);
    }

}
