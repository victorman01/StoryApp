package com.dicoding.storyapp.ui.maps

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R

import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.tools.ViewModelFactory
import com.dicoding.storyapp.ui.liststory.ListStoryViewModel
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModelFac: ViewModelFactory
    private lateinit var listStoryViewModel: ListStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModelFac = ViewModelFactory.getInstance(this)
        listStoryViewModel = ViewModelProvider(this, viewModelFac)[ListStoryViewModel::class.java]
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        val tokenUser = listStoryViewModel.getToken()
        listStoryViewModel.getStoriesLocList(tokenUser.value?.token.toString())
            .observe(this) { stories ->
                stories?.forEach { story ->
                    val lat = story.lat ?: 0.0
                    val lon = story.lon ?: 0.0
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(lat, lon))
                            .title("Story from: ${story.name}")
                    )
                }
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle(client: Context, style: Int) {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    client,
                    style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.setMapStyle(null)
                true
            }

            R.id.retro_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                setMapStyle(this, R.raw.retro_style)
                true
            }

            R.id.night_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                setMapStyle(this, R.raw.night_style)
                true
            }

            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                mMap.setMapStyle(null)
                true
            }

            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                mMap.setMapStyle(null)
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                mMap.setMapStyle(null)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}