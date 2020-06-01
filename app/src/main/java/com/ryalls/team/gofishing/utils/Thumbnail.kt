package com.ryalls.team.gofishing.utils

import android.graphics.Bitmap
import android.graphics.Matrix

class Thumbnail {

    fun decodeSampledBitmap(pathName: String?, maxWidth: Int, maxHeight: Int): Bitmap? {
        val newBitmap = ImageProcessing.decodeBitmap(pathName, maxWidth, maxHeight)
        val thumbnail = newBitmap?.rotate(pathName)
        newBitmap?.recycle()
        return thumbnail
    }


    private fun Bitmap.rotate(pathName: String?): Bitmap {
        val degrees = ImageProcessing.rotateImageIfRequired(pathName!!)
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


}