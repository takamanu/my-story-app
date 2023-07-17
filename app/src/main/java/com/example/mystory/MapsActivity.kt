package com.example.mystory

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.databinding.ActivityMapsBinding
import com.example.mystory.datamodel.LoginResponse
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var binding: ActivityMapsBinding
    private lateinit var sessionPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMapsBinding.inflate(layoutInflater)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        supportActionBar?.hide()
        val view = binding.root
        setContentView(view)

        binding.backButton.setOnClickListener {
            val potentialActivity = Intent(this@MapsActivity, MainActivity::class.java)
            startActivity(potentialActivity)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapsViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        sessionPreferences = getSharedPreferences("session", MODE_PRIVATE)


        val loginResponseJson = sessionPreferences.getString("loginResponse", null)
        val gson = Gson()

        val loginResponse = gson.fromJson(loginResponseJson, LoginResponse::class.java)

        Log.d("MainActivity", "Sampe sini masuk $loginResponse")
        val token = loginResponse.loginResult?.token
        if (token != null) {
            mapsViewModel.getAllProductsLocation(token)
        }



        // Observe the products LiveData in the ViewModel
        mapsViewModel.locations.observe(this) { locations ->
            // Clear any existing markers on the map
            mMap.clear()

            val gson = Gson() // Create a Gson instance

            // Iterate over the product list
            locations?.forEach { locations ->
                // Split the location string into latitude and longitude

                if (locations != null) {
                    val latitude = locations.lat?.toDouble()
                    val longitude = locations.lon?.toDouble()

                    // Add a marker for each location
                    val markerLocation: LatLng? = latitude?.let {
                        longitude?.let { lon ->
                            LatLng(it, lon)
                        }
                    }

                    if (markerLocation != null) {
                        mMap.addMarker(MarkerOptions().position(markerLocation).title(gson.toJson(locations.name)))
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation))

                    // Retrieve other key-value pairs using Gson
//                    val retrievedProduct = gson.fromJson(gson.toJson(product), Product::class.java)
//                    val category = retrievedProduct.category
//                    val user = retrievedProduct.user
                    // Use the retrieved values as needed
                }
            }
        }

        // Get the user's current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // Initialize fusedLocationClient if permissions are granted

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                }
            }
        }

        // Set a marker click listener
//        mMap.setOnMarkerClickListener { marker ->
//            // Get the clicked product associated with the marker
//            val clickedProduct = getProductFromMarkerTitle(marker.title)
//
////            if (clickedProduct != null) {
////                // Show the SupItemsFragment with the clicked product
////                val gson = Gson()
////                val productJson = gson.toJson(clickedProduct) // Convert the clicked product to JSON
////                val fragment = SupItemsFragment.newInstance(productJson)
////
////                val fragmentManager: FragmentManager = supportFragmentManager
////                fragmentManager.beginTransaction()
////                    .replace(android.R.id.content, fragment)
////                    .addToBackStack(null) // Optional: Add the transaction to the back stack
////                    .commit()
////            }
//
//            true
//        }
    }

}
