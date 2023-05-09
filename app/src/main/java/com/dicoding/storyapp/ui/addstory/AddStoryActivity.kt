package com.dicoding.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.tools.ViewModelFactory
import com.dicoding.storyapp.tools.createCustomTempFile
import com.dicoding.storyapp.tools.reduceFileImage
import com.dicoding.storyapp.tools.uriToFile
import com.dicoding.storyapp.ui.liststory.ListStoryActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var viewModelFac: ViewModelFactory
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_GALLERY_PERMISSION = 200
    private val REQUEST_LOCATION_PERMISSION = 1001

    private var long: Double? = null
    private var lat: Double? = null

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)
        supportActionBar?.hide()
        viewModelFac = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, viewModelFac)[AddStoryViewModel::class.java]

        addStoryBinding.buttonAddCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startTakePhoto()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }

        addStoryBinding.buttonAddGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                gallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY_PERMISSION
                )
            }
        }

        addStoryBinding.checkBoxLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getCurrentLocation()
            } else {
                locationManager.removeUpdates(locationListener)
            }
        }

        addStoryBinding.buttonAdd.setOnClickListener {
            if (addStoryBinding.edAddDescription.text.isEmpty()) {
                Toast.makeText(
                    this@AddStoryActivity,
                    getString(R.string.null_description_message),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val description = addStoryBinding.edAddDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                val tokenUser = addStoryViewModel.getToken()
                addStoryViewModel.addNewStory(
                    tokenUser.value?.token.toString(),
                    imageMultipart,
                    description,
                    lat,
                    long
                )
                val checkAdd = addStoryViewModel.checkAdd
                checkAdd.observe(this) {
                    if (it) {
                        if (long != null && lat != null) {
                            locationManager.removeUpdates(locationListener)
                        }
                        val intent = Intent(this, ListStoryActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(
                    this@AddStoryActivity,
                    getString(R.string.null_picture_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            addStoryViewModel.messages.observe(this) {
                it.getContentIfNotHandled()?.let { text ->
                    Toast.makeText(this@AddStoryActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
            addStoryViewModel.isLoading.observe(this@AddStoryActivity) {
                showLoading(it)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val bitmap = BitmapFactory.decodeFile(myFile.path)
            addStoryBinding.ivAddPhoto.setImageBitmap(bitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImageUri: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImageUri, this)
            getFile = myFile
            addStoryBinding.ivAddPhoto.setImageURI(selectedImageUri)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dicoding.storyapp.ui.addstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun gallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getCurrentLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                long = location.longitude
                lat = location.latitude
                Log.d("AddStoryActivity", "Longitude: $long, Latitude: $lat")
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun showLoading(isLoading: Boolean) {
        addStoryBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTakePhoto()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
