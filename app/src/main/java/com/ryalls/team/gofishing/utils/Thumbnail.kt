package com.ryalls.team.gofishing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

class Thumbnail {

    fun decodeSampledBitmap(pathName: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        val wRatio_inv = options.outWidth.toFloat() / maxWidth
        val hRatio_inv =
            options.outHeight.toFloat() / maxHeight // Working with inverse ratios is more comfortable
        val finalW: Int
        val finalH: Int
        val minRatio_inv /* = max{Ratio_inv} */: Int
        if (wRatio_inv > hRatio_inv) {
            minRatio_inv = wRatio_inv.toInt()
            finalW = maxWidth
            finalH = Math.round(options.outHeight / wRatio_inv)
        } else {
            minRatio_inv = hRatio_inv.toInt()
            finalH = maxHeight
            finalW = Math.round(options.outWidth / hRatio_inv)
        }
        options.inSampleSize =
            pow2Ceil(minRatio_inv) // pow2Ceil: A utility function that comes later
        options.inJustDecodeBounds = false // Decode bitmap with inSampleSize set
        val newBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeFile(pathName, options),
            finalW, finalH, true
        )
        val thumbnail = newBitmap.rotate(pathName)
        newBitmap.recycle()
        return thumbnail
    }

    /**
     * @return the largest power of 2 that is smaller than or equal to number.
     * WARNING: return {0b1000000...000} for ZERO input.
     */
    private fun pow2Ceil(number: Int): Int {
        return 1 shl -(Integer.numberOfLeadingZeros(number) + 1) // is equivalent to:
        // return Integer.rotateRight(1, Integer.numberOfLeadingZeros(number) + 1);
    }


    private fun Bitmap.rotate(pathName: String?): Bitmap {
        val degrees = ImageProcessing.rotateImageIfRequired(pathName!!)
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


}