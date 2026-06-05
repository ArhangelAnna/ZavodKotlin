package com.example.zavod.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri
    private lateinit var photoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val file = File(
            getExternalFilesDir(null),
            "photo_${System.currentTimeMillis()}.jpg"
        )

        photoPath = file.absolutePath

        imageUri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )

        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                val result = Intent().apply {
                    putExtra("path", photoPath)
                }

                setResult(RESULT_OK, result)
            } else {
                setResult(RESULT_CANCELED)
            }

            finish()
        }

        takePictureLauncher.launch(imageUri)
    }
}