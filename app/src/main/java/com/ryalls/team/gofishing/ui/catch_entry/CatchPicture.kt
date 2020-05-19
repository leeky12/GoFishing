package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.FishingPermissions
import com.ryalls.team.gofishing.utils.GalleryAdd
import com.ryalls.team.gofishing.utils.ImageProcessing
import kotlinx.android.synthetic.main.catch_picture.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


/**
 * A placeholder fragment containing a simple view.
 */
class CatchPicture : Fragment() {

    private lateinit var permissionCheck: FishingPermissions

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            permissionCheck = context as FishingPermissions
        } catch (castException: ClassCastException) {
            Log.d("WordPuzzleSolver", "Interface Not Defined")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.catch_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        takePicture.setOnClickListener {
            val granted = permissionCheck.checkPermissions()
            if (granted) {
                dispatchTakePictureIntent()
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri =
                    Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private lateinit var fileName: String
    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var photoURI: Uri

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = catchView.width
        val targetH: Int = catchView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val newBitmap = ImageProcessing.rotateImageIfRequired(bitmap, currentPhotoPath)
        catchView.setImageBitmap(newBitmap)

        // this is saving out a new rotated version of the file that fits the image view, should
        // we really be doing this as this will overwrite the original full size image? This can
        // of course be done in a coroutine so the user doesn't see any delay in the image being
        // displayed

        // did we rotate?
        if (newBitmap != bitmap) {
            saveImage(newBitmap)
        }
    }

    private fun saveImage(bitmap: Bitmap?) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (bitmap != null) {
                        FileOutputStream(currentPhotoPath).use { out ->
                            bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                out
                            )
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("dd-MM-yyyy_H-mm-ss").format(Date())

        fileName = "Catch_${timeStamp}.jpg"

        val test = activity?.filesDir

        val storageDir: File? = test
        val location = storageDir?.absolutePath + "/junk.jpg"

        val file = File(location)
        file.createNewFile()
        currentPhotoPath = location
        return file
    }

    private fun dispatchTakePictureIntent() {
        val pm = activity?.packageManager
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            if (pm != null) {
                takePictureIntent.resolveActivity(pm)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.ryalls.team.gofishing",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            setPic()
            GalleryAdd.galleryAddPic(
                requireActivity(),
                currentPhotoPath,
                BitmapFactory.decodeFile(currentPhotoPath),
                fileName
            )
        }
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(parentFragment: FishingPermissions): CatchPicture {
            val args = Bundle()
            val f = CatchPicture()
            f.permissionCheck = parentFragment
            return f
        }
    }
}