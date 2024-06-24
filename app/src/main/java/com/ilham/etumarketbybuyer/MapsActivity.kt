package com.ilham.etumarketbybuyer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ilham.etumarketbybuyer.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity() {
    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var fareTextView: TextView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val costPerKm = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val webView: WebView = findViewById(R.id.webView)
        val fareTextView: TextView = findViewById(R.id.fareTextView)

        // Atur klien web untuk memuat URL dalam aplikasi, bukan mengarahkan ke browser eksternal
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.startsWith("intent://")) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent != null) {
                            startActivity(intent)
                            return true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (url.contains("directions")) {
                    calculateCost(url)
                }
                view?.loadUrl(url)
                return true
            }
        }

        // Aktifkan fitur ChromeClient untuk mendukung pemutaran video, dll.
        webView.webChromeClient = WebChromeClient()

        // Dapatkan Uri dari intent
        val data: Uri? = intent?.data
        if (data != null) {
            val query = data.getQueryParameter("query")
            if (!query.isNullOrEmpty()) {
                // Membuat URL dari koordinat yang didapat dari intent
                val url = "https://www.google.com/maps?q=$query"
                webView.loadUrl(url)
            }
        }

        // Aktifkan fungsi zoom dan aktivasi JavaScript dalam WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        // Check and request location permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//
//        }

        checkLocationPermission()


    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    showLocationErrorDialog()
                }
            }.addOnFailureListener {
                showLocationErrorDialog()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    private fun calculateCost(url: String) {
        // Parsing URL untuk mendapatkan tujuan
        val fareTextView: TextView = findViewById(R.id.fareTextView)
        val destination = getDestinationFromUrl(url)

        if (currentLocation != null && destination != null) {
            val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLatLng.latitude, currentLatLng.longitude,
                destination.latitude, destination.longitude,
                results
            )
            val distanceInKm = results[0] / 1000
            val cost = distanceInKm * costPerKm.toDouble()
            fareTextView.text = cost.toString()
            showCostDialog(cost)
            updateFareTextView(cost)

        }
    }

    private fun showLocationErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Kesalahan Lokasi")
            .setMessage("Google Maps tidak bisa menentukan lokasi Anda")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getDestinationFromUrl(url: String): LatLng? {
        // Ekstrak koordinat tujuan dari URL
        // Contoh URL: "https://www.google.com/maps/dir/?api=1&destination=-6.175110,106.865039"
        val uri = Uri.parse(url)
        val destination = uri.getQueryParameter("destination")
        return destination?.split(",")?.let {
            if (it.size == 2) {
                LatLng(it[0].toDouble(), it[1].toDouble())
            } else null
        }
    }

    private fun showCostDialog(cost: Double) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Biaya Perjalanan")
            .setMessage("Biaya perjalanan Anda adalah Rp $cost")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    private fun updateFareTextView(cost: Double) {
        fareTextView.text = "Biaya: Rp $cost"
        fareTextView.visibility = android.view.View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                // Permission denied
                AlertDialog.Builder(this)
                    .setTitle("Izin Lokasi Diperlukan")
                    .setMessage("Aplikasi ini memerlukan izin lokasi untuk menghitung biaya perjalanan.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }
}