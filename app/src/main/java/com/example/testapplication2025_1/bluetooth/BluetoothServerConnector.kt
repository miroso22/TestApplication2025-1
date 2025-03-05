package com.example.testapplication2025_1.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.io.IOException

class BluetoothServerConnector(
    private val adapter: BluetoothAdapter,
    private val scope: CoroutineScope,
    private val onSocketConnected: (BluetoothSocket) -> Unit
) {
    private var serverSocket: BluetoothServerSocket? = null
    private var acceptConnectionJob: Job? = null

    @SuppressLint("MissingPermission")
    fun acceptConnections() {
        if (acceptConnectionJob?.isActive == true) return
        serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(BluetoothService.NAME, BluetoothService.MY_UUID)
        acceptConnectionJob = scope.launch {
            while (true) {
                ensureActive()
                val socket = try {
                    serverSocket?.accept()
                } catch (e: IOException) {
                    Log.e("Bluetooth", e.stackTraceToString())
                    break
                } ?: continue

                onSocketConnected(socket)
                serverSocket?.close()
                break
            }
        }
    }

    fun close() {
        acceptConnectionJob?.cancel()
        serverSocket?.close()
    }
}