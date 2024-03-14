package com.example.particleapped.ui.particleAppScreen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.particleapped.R
import com.example.particleapped.utils.ImageUtils


class MyCameraActivity : Activity() {
    private var documentImageBitmap: Bitmap? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val byteArray = intent.getByteArrayExtra("imageData")
        documentImageBitmap = ImageUtils.byteArrayToBitmap(byteArray)
        val cameraIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val photo = data.extras!!["data"] as Bitmap?
            if (documentImageBitmap == null || photo == null) {
                onBackPressed()
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
                return
            }
            if (FaceComparator.areFacesSimilar(photo, documentImageBitmap!!))
                FaceComparator.areFacesSimilar(photo, documentImageBitmap!!)
        }
    }

    companion object {
        private const val CAMERA_REQUEST = 1888
    }
}