package com.everybodv.storyapp.view.ui

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.databinding.ActivityUploadBinding
import com.everybodv.storyapp.util.*
import com.everybodv.storyapp.view.model.AuthViewModel
import com.everybodv.storyapp.view.model.StoriesViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var storiesViewModel: StoriesViewModel
    private lateinit var authPreferences: AuthPreferences

    private var getFile: File? = null

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Const.CAMERAX_RESULT) {
            @Suppress("DEPRECATION")
            val myFile = result.data?.getSerializableExtra("Picture") as File
            val isBackCamera = result.data?.getBooleanExtra("IsBackCamera", true) as Boolean

            myFile.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivImageStory.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }

        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this)
            getFile = myFile
            binding.ivImageStory.setImageURI(selectedImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.post_story)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this, Const.REQ_PERMISSION, Const.REQ_CODE
            )
        }

        authPreferences = AuthPreferences(this)
        authViewModel = ViewModelProvider(this, PreferencesFactory(authPreferences))[AuthViewModel::class.java]
        storiesViewModel = ViewModelProvider(this)[StoriesViewModel::class.java]

        binding.ibFromGallery.setSafeOnClickListener { gallery() }
        binding.ibCamerax.setSafeOnClickListener { cameraX() }
        binding.ibUpload.setSafeOnClickListener { upload() }
    }

    private fun allPermissionGranted() = Const.REQ_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun cameraX() {
        val toCameraIntent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(toCameraIntent)
    }

    private fun gallery() {
        val galleryIntent = Intent()
        galleryIntent.action = ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val chooser = Intent.createChooser(galleryIntent, getString(R.string.select_photo))
        launcherIntentGallery.launch(chooser)
    }

    private fun upload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, reqImage)
            val description = binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())

            if (binding.etDesc.text.isEmpty()) {
                binding.etDesc.error = getString(R.string.add_a_desc)
            } else {
                authViewModel.getToken().observe(this) { token ->
                    storiesViewModel.uploadStory("Bearer $token", imageMultiPart, description, this)
                    storiesViewModel.isEnabled.observe(this){ isEnabled ->
                        binding.ibUpload.isEnabled = isEnabled
                    }
                    storiesViewModel.isLoading.observe(this){ isLoading ->
                        showLoading(binding.progressBar2, isLoading)
                    }
                    storiesViewModel.storyResponse.observe(this) { response ->
                        if (!response.error) {
                            showToast(this, getString(R.string.success_upload))
                            this.getSharedPreferences("data", 0).edit().clear().apply()
                            val toMainIntent = Intent(this, MainActivity::class.java)
                            toMainIntent.putExtra(Const.SUCCESS, true)
                            toMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(toMainIntent)
                            finish()
                        }
                    }
                }
            }
        } else showToast(this, getString(R.string.pls_fill_desc_photo))
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Const.REQ_CODE) {
            if (!allPermissionGranted()) {
                showToast(this, getString(R.string.permission_not_granted))
                finish()
            }
        }
    }
}