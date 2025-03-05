package com.example.testapplication2025_1.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.example.testapplication2025_1.bluetooth.BluetoothService.Companion.MY_UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BluetoothClientConnector(
    private val scope: CoroutineScope,
    private val onSocketConnected: (BluetoothSocket) -> Unit
) {
    private var clientSocket: BluetoothSocket? = null
    private var connectionJob: Job? = null

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        if (connectionJob?.isActive == true) return
        val socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
            .also { clientSocket = it } ?: return

        connectionJob = scope.launch {
            socket.connect()
            onSocketConnected(socket)
        }
    }

    fun close() {
        connectionJob?.cancel()
        clientSocket?.close()
    }
}