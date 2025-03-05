package com.example.testapplication2025_1.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext

@Composable
fun listenToAvailableBluetoothDevices(): State<List<BluetoothDevice>> {
    val context = LocalContext.current
    return produceState(listOf()) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != BluetoothDevice.ACTION_FOUND) return
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) ?: return
                val macAddresses = value.map { it.address }
                if (device.address !in macAddresses)
                    value += device
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        awaitDispose { context.unregisterReceiver(receiver) }
    }
}