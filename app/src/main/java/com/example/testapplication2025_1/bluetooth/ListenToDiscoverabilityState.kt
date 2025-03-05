package com.example.testapplication2025_1.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext

@Composable
fun listenToDiscoverabilityState(): State<Boolean> {
    val context = LocalContext.current
    return produceState(false) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) return
                val scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1)
                value = scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
            }
        }
        val filter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitDispose { context.unregisterReceiver(receiver) }
    }
}