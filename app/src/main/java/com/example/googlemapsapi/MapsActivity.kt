package com.example.googlemapsapi

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ZoomControls
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val zoom = findViewById(R.id.zoom) as ZoomControls
        zoom.setOnZoomOutClickListener { mMap!!.animateCamera(CameraUpdateFactory.zoomOut()) }
        zoom.setOnZoomInClickListener { mMap!!.animateCamera(CameraUpdateFactory.zoomIn()) }


        val btn_MapType = findViewById(R.id.btn_Uydu) as Button
        btn_MapType.setOnClickListener {
            if (mMap!!.mapType == GoogleMap.MAP_TYPE_NORMAL) {
                mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                btn_MapType.text = "Özel"
            } else {
                mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                btn_MapType.text = "Uydu"
            }
        }

        val btnGo = findViewById(R.id.btn_Git) as Button

        btnGo.setOnClickListener {
            val etLocation = findViewById(R.id.edt_location) as EditText
            val location = etLocation.text.toString()
            if (location != null && location != "") {
                var adressList: List<Address>? = null
                val geocoder = Geocoder(this@MapsActivity)
                try {
                    adressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val address = adressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap!!.addMarker(MarkerOptions().position(latLng).title("$location"))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val turkey = LatLng(39.925533, 32.866287)
        mMap.addMarker(MarkerOptions().position(turkey).title("TÜRKİYE"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(turkey))

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap!!.isMyLocationEnabled = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MapsActivity.REQUEST_lOCATION)
            }
        }

        try {

            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps))

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_lOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap!!.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(applicationContext, "Konum izni verilmedi.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        val REQUEST_lOCATION = 90
    }
}
