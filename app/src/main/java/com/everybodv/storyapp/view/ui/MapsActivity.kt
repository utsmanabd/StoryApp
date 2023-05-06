package com.everybodv.storyapp.view.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.data.repository.MapsRepository
import com.everybodv.storyapp.databinding.ActivityMapsBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.util.PreferencesFactory
import com.everybodv.storyapp.view.model.AuthViewModel
import com.everybodv.storyapp.view.model.MapsViewModel
import com.everybodv.storyapp.view.model.Token
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var authPreferences: AuthPreferences
    private lateinit var mapsRepository: MapsRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var token: Token

    private val mapsViewModel: MapsViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepository, this)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    private val reqPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.location)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mapsModel()
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-1.348994, 115.6112)))

        listLocation = intent.getParcelableArrayListExtra(Const.LIST_LOCATION)
        listUserName = intent.getStringArrayListExtra(Const.LIST_USER_NAME)

        setMapStyle()
        getMyLocation()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun mapsModel() {
        authPreferences = AuthPreferences(this)
        authRepository = AuthRepository()
        mapsRepository = MapsRepository()
        token = Token(authPreferences)

        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepository, this)
        )[AuthViewModel::class.java]
        token.getToken().observe(this) { token ->
            if (token != null) {
                mapsViewModel.getStoryWithLoc("Bearer $token")
                mapsViewModel.getStories().observe(this) { stories ->
                    stories?.let { story ->
                        for (i in story.listIterator()) {
                            val latLng = LatLng(i.lat!!, i.lon!!)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(i.name)
                                    .snippet(i.description)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            reqPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.normal -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.terrain -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            R.id.satellite -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.hybrid -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "MapsActivity"
    }

}