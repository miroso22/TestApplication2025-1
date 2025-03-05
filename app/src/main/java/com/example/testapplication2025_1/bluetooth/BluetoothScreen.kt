package com.example.testapplication2025_1.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testapplication2025_1.requestPermissions
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private val requiredPermissions = listOf(
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.ACCESS_FINE_LOCATION
)

@Composable
fun BluetoothScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current

    val hasPermissions by requestPermissions(requiredPermissions)
    if (!hasPermissions) return

    val service = remember { BluetoothService(context) }
    val isBluetoothReady by listenToBluetoothState(initial = service.isBluetoothReady)
    val isConnected by service.isConnected.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        if (!isBluetoothReady)
            service.requestPermission(context)
    }
    DisposableEffect(Unit) {
        onDispose { service.stop() }
    }

    BackHandler(onBack = onBack)

    if (!isBluetoothReady) return

    if (isConnected) {
        Chat(service, modifier)
    } else {
        DeviceList(service, modifier)
    }
}

@Composable
private fun DeviceList(
    service: BluetoothService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDeviceDiscoverable by listenToDiscoverabilityState()
    val isDiscovering by listenToDiscoveryState()
    val availableDevices by listenToAvailableBluetoothDevices()

    DisposableEffect(Unit) {
        service.acceptConnections()
        onDispose { service.stopAcceptingConnections() }
    }

    LazyColumn(modifier = modifier) {
        item {
            Text("Paired devices")
        }
        items(service.pairedDevices) {
            BluetoothDeviceItem(it) { service.connect(it) }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Available devices")
                if (isDeviceDiscoverable) {
                    Text("Device is visible")
                } else {
                    Button(
                        onClick = { service.makeDeviceDiscoverable(context) },
                        content = { Text("Make visible") }
                    )
                }
                if (isDiscovering) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { service.startDiscovery() },
                        content = { Text("Search") }
                    )
                }
            }
        }
        items(availableDevices) {
            BluetoothDeviceItem(it) { service.connect(it) }
        }
    }
}

@Composable
private fun Chat(
    service: BluetoothService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        service.inputMessages.onEach { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }.launchIn(this)

        service.messageSentSignal.onEach { success ->
            val message = if (success) "Message sent" else "Failed to send message"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }.launchIn(this)
    }

    Row(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = text,
            onValueChange = { text = it },
            maxLines = 1
        )
        Button(
            onClick = {
                service.sendMessage(text)
                text = ""
            },
            content = { Text("Send") }
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun BluetoothDeviceItem(device: BluetoothDevice, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = device.name ?: device.address
        )
        HorizontalDivider()
    }
}