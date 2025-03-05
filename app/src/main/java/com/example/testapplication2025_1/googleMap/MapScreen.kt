package com.example.testapplication2025_1.googleMap

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.random.Random

private val kyiv = LatLng(50.45, 30.52)

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val markers = remember { mutableStateListOf(MarkerState(kyiv)) }
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kyiv, 10f)
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    BackHandler(onBack = onBack)

    LocationFetcher { location ->
        camera.position = CameraPosition.fromLatLngZoom(location, 10f)
        userLocation = location
    }

    Column(modifier = Modifier.padding(vertical = 60.dp)) {
        PlacesSearchBar { location ->
            camera.position = CameraPosition.fromLatLngZoom(location, 10f)
            markers.add(MarkerState(location))
        }

        GoogleMap(
            modifier = modifier.fillMaxWidth().weight(1f),
            cameraPositionState = camera
        ) {
            markers.forEach { marker ->
                Marker(
                    state = marker,
                    title = "Remove",
                    onInfoWindowClick = { markers.remove(marker) }
                )
            }

            userLocation?.let {
                Circle(center = it, radius = 40.0)
            }
        }

        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    camera.position = CameraPosition.fromLatLngZoom(getRandomCords(), 10f)
                },
                content = { Text("Go to random place") }
            )
            Button(
                modifier = Modifier.weight(1f),
                onClick = { markers.add(MarkerState(camera.position.target)) },
                content = { Text("Add marker") }
            )
        }
    }
}

private fun getRandomCords() =
    LatLng(Random.nextDouble(-90.0, 90.0), Random.nextDouble(-180.0, 180.0))