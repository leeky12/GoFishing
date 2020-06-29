package com.ryalls.team.gofishing.ui.catch_entry

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.ryalls.team.gofishing.R
import com.ryalls.team.gofishing.interfaces.FishingPermissions
import com.ryalls.team.gofishing.utils.GalleryAdd
import com.ryalls.team.gofishing.utils.KeyboardUtils
import kotlinx.android.synthetic.main.catch_picture.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class CatchPicture : Fragment() {

    private lateinit var permissionCheck: FishingPermissions
    private val viewModel: CatchDetailsViewModel by activityViewModels()
    private var bitmap: Bitmap? = null

    private var mediaPath: String = ""
    private lateinit var currentPhotoPath:
            String
    private lateinit var fileName: String
    private val requestTakePhoto = 1
    private lateinit var photoURI: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.catch_picture, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        takePicture.setOnClickListener {
            val granted = permissionCheck.checkFishingPermissions()
            if (granted) {
                dispatchTakePictureIntent()
            }
        }
        if (viewModel.catchRecord.imageID.isNotEmpty()) {
            currentPhotoPath = viewModel.catchRecord.imageID
            mediaPath = currentPhotoPath
        }
        KeyboardUtils().closeKeyboard(requireContext(), view)
    }

    override fun onResume() {
        super.onResume()
        KeyboardUtils().closeKeyboard(requireContext(), view)
        setPic()
    }

    private fun setPic() {
        if (mediaPath.isNotEmpty()) {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height: Int = displayMetrics.heightPixels
            val width: Int = displayMetrics.widthPixels
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val content = GalleryAdd.parseAllImages(requireActivity(), mediaPath)
                val resolver = requireContext().contentResolver
                if (content.path?.isNotEmpty()!!) {
                    Glide.with(this).load(content).override(width, height).into(catchView)
                } else {
                    Snackbar.make(
                        requireView(),
                        "Original catch picture has been removed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                val file = File(mediaPath)
                if (file.exists()) {
                    Glide.with(this).load(mediaPath).override(width, height).into(catchView)
                    Log.d("BitmapMetrics", "Bitmap " + (bitmap?.height) + " " + (bitmap?.width))
                    Log.d("BitmapMetrics", "Screen $height $width")
                } else {
                    bitmap = null
                    Snackbar.make(
                        requireView(),
                        "Original catch picture has been removed",
                        Snackbar.LENGTH_LONG
                    ).show()
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
        try {
            file.delete()
        } catch (ie: IOException) {
            // it's ok if the file isnt there to delete
        }
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
                        startActivityForResult(takePictureIntent, requestTakePhoto)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestTakePhoto && resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.setThumbnail(currentPhotoPath)
            mediaPath = GalleryAdd.galleryAddPic(
                requireActivity(),
                currentPhotoPath,
                fileName
            )
            setPic()
            viewModel.catchRecord.imageID = mediaPath
        }
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(parentFragment: FishingPermissions): CatchPicture {
            val f = CatchPicture()
            f.permissionCheck = parentFragment
            return f
        }
    }
}