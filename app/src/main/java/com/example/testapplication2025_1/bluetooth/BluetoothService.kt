package com.example.testapplication2025_1.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothService(context: Context) {
    private val manager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter = manager.adapter ?: throw Exception("Device doesn't support Bluetooth")
    private val scope = CoroutineScope(Dispatchers.IO)

    private val serverConnector = BluetoothServerConnector(adapter, scope) { socket ->
        this.socket.value = socket
    }
    private val clientConnector = BluetoothClientConnector(scope) { socket ->
        this.socket.value = socket
    }
    private var messageHandler: BluetoothMessageHandler? = null

    val isBluetoothReady get() = adapter.isEnabled
    val pairedDevices by lazy { adapter.bondedDevices.toList() }

    private val socket = MutableStateFlow<BluetoothSocket?>(null)
    val isConnected = socket.map { it != null }
    val inputMessages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messageSentSignal = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    init {
        socket.filterNotNull().onEach {
            messageHandler = BluetoothMessageHandler(it, inputMessages, messageSentSignal, scope)
        }.launchIn(scope)
    }

    fun requestPermission(context: Context) {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        context.startActivity(intent)
    }

    fun startDiscovery() {
        val started = adapter.startDiscovery()
        val message = if (started) "Device Discovery started!" else "Device Discovery failed to start!"
        Log.d("Bluetooth Service", message)
    }
    fun stopDiscovery() = adapter.cancelDiscovery()

    fun makeDeviceDiscoverable(context: Context) {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        context.startActivity(intent)
    }

    fun acceptConnections() = serverConnector.acceptConnections()
    fun stopAcceptingConnections() = serverConnector.close()

    fun connect(device: BluetoothDevice) {
        adapter.cancelDiscovery()
        serverConnector.close()
        clientConnector.connect(device)
    }
    fun stopConnecting() = clientConnector.close()

    fun sendMessage(message: String) = messageHandler?.sendMessage(message)

    fun stop() {
        adapter.cancelDiscovery()
        serverConnector.close()
        clientConnector.close()
        messageHandler?.close()
        scope.cancel()
    }

    companion object {
        const val NAME = "BluetoothChatInsecure"
        val MY_UUID: UUID = UUID.fromString("689ed85b-846f-4b32-8af1-f0dee6eceae9")
    }
}