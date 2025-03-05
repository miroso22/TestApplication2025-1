package com.example.testapplication2025_1.googleMap

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@SuppressLint("MissingPermission")
@Composable
fun LocationFetcher(onLocationAquired: (LatLng) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun requestLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location ?: return@addOnSuccessListener
            val userLatLng = LatLng(location.latitude, location.longitude)
            onLocationAquired(userLatLng)
        }
    }

    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) requestLocation()
    }

    LaunchedEffect(Unit) {
        if (context.hasLocationPermission())
            requestLocation()
        else
            permissionRequestLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}


private fun Context.hasLocationPermission() =
    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED