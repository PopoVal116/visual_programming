package com.example.visual_programming2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class LocationActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 100
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        tvLat = findViewById(R.id.tv_lat)
        tvLon = findViewById(R.id.tv_lon)
        tvAlt = findViewById(R.id.tv_alt)
        tvTime = findViewById(R.id.tv_time)

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_update).setOnClickListener { getLastLocation() }
    }

    private fun getLastLocation() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        updateUI(location)
                    } else {
                        Toast.makeText(this, "Нет данных о местоположении", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Нет доступа к геолокации", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(location: Location) {
        val time = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(location.time))
        val altitude = if (location.hasAltitude()) location.altitude else 0.0

        tvLat.text = "%.6f".format(location.latitude)
        tvLon.text = "%.6f".format(location.longitude)
        tvAlt.text = if (altitude != 0.0) "%.1f м".format(altitude) else "—"
        tvTime.text = time
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }
}