package com.example.testwidetech

import android.R.string
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileOutputStream


class ContactActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var outputStream: FileOutputStream
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback:LocationCallback? = null


    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        setupListeners()
        File(applicationContext.getFilesDir(), "log_coord")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest!!.apply {
            interval = 7500
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation.run {
                    val latLng = LatLng(latitude, longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f), 1000, null)
                    val coordinates = "${latLng.latitude} -" + "${latLng.longitude}"
                        outputStream = openFileOutput("log_coord", MODE_PRIVATE)
                        outputStream.write(coordinates.toByteArray())
                        outputStream.close()
                }
            }
        }
        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        requestLocationPermission()
        startLocationUpdates()
    }

    private fun locationPermissionCheck() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun requestLocationPermission() {
        if (!::map.isInitialized) return
        if (locationPermissionCheck()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!locationPermissionCheck()) {
            map.isMyLocationEnabled == false
            Toast.makeText(
                applicationContext,
                "Debes Aceptar los Permisos para Usar esta Funcion ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    applicationContext,
                    "Debes Aceptar los Permisos para Usar esta Funcion ",
                    Toast.LENGTH_SHORT
                ).show()
            } else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        if(true) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
    }

    private fun setupListeners(){
        var backArrowContact : ImageView = findViewById(R.id.backArrowContact)
        backArrowContact.setOnClickListener{
            val profileIntent: Intent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
            finish()
        }
    }
}