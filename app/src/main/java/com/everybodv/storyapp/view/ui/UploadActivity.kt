package com.everybodv.storyapp.view.ui

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.data.repository.UploadRepository
import com.everybodv.storyapp.databinding.ActivityUploadBinding
import com.everybodv.storyapp.util.*
import com.everybodv.storyapp.view.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepository: AuthRepository
    private lateinit var uploadRepository: UploadRepository

    private lateinit var token: Token
    private lateinit var authViewModel: AuthViewModel
    private val uploadViewModel: UploadViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepository, this)
    }

    private var location: LatLng? = null
    private var getFile: File? = null

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                location()
            }
        }

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
        authRepository = AuthRepository()
        uploadRepository = UploadRepository()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        token = Token(authPreferences)
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepository, this)
        )[AuthViewModel::class.java]

        binding.ibFromGallery.setSafeOnClickListener { gallery() }
        binding.ibCamerax.setSafeOnClickListener { cameraX() }
        binding.ibUpload.setSafeOnClickListener { upload() }
        binding.swLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                location()
            }
        }
    }

    private fun location() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) location = LatLng(loc.latitude, loc.longitude)
            }
        } else {
            showToast(this, getString(R.string.loc_permission))
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
                "photo", file.name, reqImage
            )
            val description =
                binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())

            if (binding.etDesc.text.isEmpty()) {
                binding.etDesc.error = getString(R.string.add_a_desc)
            } else {
                token.getToken().observe(this) { token ->
                    if (location != null) {
                        uploadViewModel.uploadStory(
                            "Bearer $token", imageMultiPart, description,
                            lat = (location as LatLng).latitude,
                            lon = (location as LatLng).longitude
                        )
                    } else {
                        uploadViewModel.uploadStory(
                            "Bearer $token",
                            imageMultiPart,
                            description
                        )
                    }
                    uploadViewModel.isEnabled.observe(this) { isEnabled ->
                        binding.ibUpload.isEnabled = isEnabled
                    }
                    uploadViewModel.isLoading.observe(this) { isLoading ->
                        showLoading(binding.progressBar2, isLoading)
                    }
                    uploadViewModel.storyResponse.observe(this) { response ->
                        if (!response.error) {
                            showToast(this, getString(R.string.success_upload))
                            this.getSharedPreferences("data", 0).edit().clear().apply()
                            val toMainIntent = Intent(this, MainActivity::class.java)
                            toMainIntent.putExtra(Const.SUCCESS, true)
                            toMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(toMainIntent)
                            finish()
                        } else {
                            showToast(this, getString(R.string.failed_upload))
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