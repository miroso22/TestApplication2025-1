package com.example.testapplication2025_1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testapplication2025_1.bluetooth.BluetoothScreen
import com.example.testapplication2025_1.googleMap.MapScreen
import com.example.testapplication2025_1.notifications.NotificationsManager
import com.example.testapplication2025_1.notifications.NotificationsScreen

private enum class Feature {
    GoogleMap,
    Bluetooth,
    Notifications
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var feature by remember { mutableStateOf<Feature?>(null) }

    when (feature) {
        Feature.GoogleMap -> MapScreen(modifier) { feature = null }
        Feature.Bluetooth -> BluetoothScreen(modifier) { feature = null }
        Feature.Notifications -> NotificationsScreen(modifier) { feature = null }
        null -> SelectionScreen { feature = it }
    }
}

@Composable
private fun SelectionScreen(modifier: Modifier = Modifier, onFeatureSelected: (Feature) -> Unit) {
    Column(modifier.padding(vertical = 40.dp)) {
        Feature.entries.forEach { feature ->
            Button(onClick = { onFeatureSelected(feature) }) {
                Text(feature.name)
            }
        }
    }
}