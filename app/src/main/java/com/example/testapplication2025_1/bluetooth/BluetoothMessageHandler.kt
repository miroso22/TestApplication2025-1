package com.example.testapplication2025_1.bluetooth

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothMessageHandler(
    private val socket: BluetoothSocket,
    private val messages: MutableSharedFlow<String>,
    private val messageSentSignal: MutableSharedFlow<Boolean>,
    scope: CoroutineScope
) {
    private var input: InputStream = socket.inputStream
    private var output: OutputStream = socket.outputStream
    private val buffer: ByteArray = ByteArray(1024)

    private val inputMessagesJob: Job = scope.launch {
        var numBytes: Int

        while (true) {
            ensureActive()
            numBytes = try {
                input.read(buffer)
            } catch (e: IOException) {
                break
            }
            val message = String(buffer, 0, numBytes)
            messages.tryEmit(message)
        }
    }

    fun sendMessage(message: String) {
        val bytes = message.toByteArray()
        val result = try {
            output.write(bytes)
            true
        } catch (e: IOException) {
            false
        }
        messageSentSignal.tryEmit(result)
    }

    fun close() {
        inputMessagesJob.cancel()
        socket.close()
    }
}