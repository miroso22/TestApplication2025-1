package com.example.testapplication2025_1.bluetooth

import android.bluetooth.BluetoothAdapter
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
fun listenToDiscoveryState(): State<Boolean> {
    val context = LocalContext.current
    return produceState(false) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                value = when (intent.action) {
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> true
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> false
                    else -> return
                }
            }
        }
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        val filter2 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, filter1)
        context.registerReceiver(receiver, filter2)

        awaitDispose { context.unregisterReceiver(receiver) }
    }
}