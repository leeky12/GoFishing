package com.ryalls.team.gofishing.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object GalleryAdd {

    fun galleryAddPic(
        activity: Activity,
        currentPhotoPath: String,
        decodeFile: Bitmap,
        fileName: String
    ) {
        val fos: OutputStream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = activity.contentResolver
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                fileName
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + "fishy"
            )
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = resolver.openOutputStream(imageUri!!)!!
        } else {

            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "fishy"
            )

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(
                        "Please Work",
                        "failed to create directory"
                    )
                    return
                }
            }

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val scanLoc =
                "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/fishy/" + fileName
            val f = File(scanLoc)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            activity.sendBroadcast(mediaScanIntent)

            fos = FileOutputStream(f)
        }

        //       just need to copy don't need to decode again

        val file = File(currentPhotoPath)
        val ins: InputStream = file.inputStream()
        ins.copyTo(fos)

        Objects.requireNonNull(fos).close()
        Objects.requireNonNull(ins).close()

        decodeFile.recycle()

        // tidy up the temporary file we needed
        val remove = File(currentPhotoPath)
        remove.delete()

    }
}