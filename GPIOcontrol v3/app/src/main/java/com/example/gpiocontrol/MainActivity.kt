package com.example.gpiocontrol

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gpiocontrol.ui.theme.GPIOcontrolTheme
import kotlinx.coroutines.*
import android.serialport.SerialPort
import java.io.*

class MainActivity : ComponentActivity() {

    private var serialPort: SerialPort? = null
    private var uartOutput: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // ✅ Always call the superclass method

        setContent {
            GPIOcontrolTheme {
                MainScreen(
                    onStartSending = { sendCsvData() }
                )
            }
        }

        // Initialize UART
        initUart()
    }

    /**
     * Initializes the UART port.
     */
    private fun initUart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val process = Runtime.getRuntime().exec("su")
                val os = process.outputStream
                os.write("chmod 666 /dev/ttyS1\n".toByteArray())
                os.write("exit\n".toByteArray())
                os.flush()
                os.close()
                process.waitFor()

                serialPort = SerialPort(File("/dev/ttyS1"), 19200, 0, 8, 1, 0)
                uartOutput = serialPort?.outputStream

                Log.d("UART", "UART initialized on /dev/ttyS1 at 19200 baud with root access")
            } catch (e: Exception) {
                Log.e("UART", "Error initializing UART: ${e.message}")
            }
        }
    }

    /**
     * Reads UART data from a CSV file and sends raw binary over UART.
     */
    private fun sendCsvData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filePath = "/storage/emulated/0/Documents/fullsystemstandby.csv"
                val file = File(filePath)
                if (!file.exists()) {
                    Log.e("CSV", "CSV file not found: $filePath")
                    return@launch
                }

                val bufferedReader = BufferedReader(FileReader(file))
                var lastTimestamp = 0.0

                bufferedReader.useLines { lines ->
                    lines.drop(1).forEach { line -> // Skip CSV header
                        val parts = line.split(",")
                        if (parts.size >= 2) {
                            val time = parts[0].toDoubleOrNull() ?: 0.0
                            val binaryTx = parts[1].trim()

                            // Ensure the binary string is valid
                            if (!binaryTx.matches(Regex("^[01]+$"))) {
                                Log.e("CSV", "Invalid binary format: $binaryTx")
                                return@forEach
                            }

                            // Calculate delay based on time difference
                            val delayTime = ((time - lastTimestamp) * 1000).toLong() // Convert to milliseconds
                            if (delayTime > 0) {
                                delay(delayTime)
                            }
                            lastTimestamp = time

                            // Convert binary to bytes and send over UART
                            sendBinaryMessage(binaryTx)
                        }
                    }
                }
                Log.d("CSV", "Finished sending CSV data.")
            } catch (e: Exception) {
                Log.e("CSV", "Error reading CSV file: ${e.message}")
            }
        }
    }

    /**
     * Sends a binary string as raw binary over UART.
     */
    private fun sendBinaryMessage(binary: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val byteData = binaryToByteArray(binary)
                uartOutput?.write(byteData)
                uartOutput?.flush()

                Log.d("UART", "Sent Binary: $binary")
            } catch (e: Exception) {
                Log.e("UART", "Error sending UART binary data: ${e.message}")
            }
        }
    }

    /**
     * Converts a binary string to a byte array.
     */
    private fun binaryToByteArray(binary: String): ByteArray {
        val byteArray = mutableListOf<Byte>()
        binary.chunked(8).forEach { byteChunk ->
            val byteValue = byteChunk.toInt(2).toByte()
            byteArray.add(byteValue)
        }
        return byteArray.toByteArray()
    }
}

/**
 * Composable UI for starting UART communication.
 */
@Composable
fun MainScreen(
    onStartSending: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Start Button
        Button(
            onClick = { onStartSending() },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Start UART Transmission")
        }
    }
}
