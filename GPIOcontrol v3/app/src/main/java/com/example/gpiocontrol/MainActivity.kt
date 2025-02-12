package com.example.gpiocontrol

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gpiocontrol.ui.theme.GPIOcontrolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.serialport.SerialPort
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {

    // UART objects
    private var serialPort: SerialPort? = null
    private var uartOutput: OutputStream? = null
    private var uartInput: InputStream? = null

    // State to hold received messages
    private val receivedMessages = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GPIOcontrolTheme {
                MainScreen(
                    receivedMessages = receivedMessages,
                    onSendHexClicked = { hexInput -> sendHexFromInput(hexInput) }
                )
            }
        }

        // Initialize UART
        initUart()
        // Read UART data
        readUartData()
    }

    /**
     * Initializes the UART port.
     */
    private fun initUart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Request root access before opening serial port
                val process = Runtime.getRuntime().exec("su")
                val os = process.outputStream
                os.write("chmod 666 /dev/ttyS1\n".toByteArray())
                os.write("exit\n".toByteArray())
                os.flush()
                os.close()
                process.waitFor()

                // Open serial port
                serialPort = SerialPort(File("/dev/ttyS1"), 19200, 0, 8, 1, 0)
                uartOutput = serialPort?.outputStream
                uartInput = serialPort?.inputStream

                Log.d("UART", "UART initialized on /dev/ttyS1 at 19200 baud with root access")
            } catch (e: Exception) {
                Log.e("UART", "Error initializing UART with root: ${e.message}")
            }
        }
    }

    /**
     * Reads UART data and updates the received messages.
     */
    private fun readUartData() {
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val bytesRead = uartInput?.read(buffer) ?: -1
                    if (bytesRead > 0) {
                        val receivedData = buffer.copyOfRange(0, bytesRead)
                        val receivedHex = receivedData.joinToString(" ") { "0x${it.toUByte().toString(16)}" }
                        Log.d("UART", "Received: $receivedHex")

                        // Update UI with received data
                        runOnUiThread {
                            receivedMessages.add(receivedHex)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UART", "Error reading UART: ${e.message}")
                    break
                }
            }
        }
    }

    /**
     * Sends hex input from the UI.
     */
    private fun sendHexFromInput(hexInput: String) {
        val byteArray = parseHexInput(hexInput)
        if (byteArray != null) {
            sendMessage(byteArray)
        } else {
            Toast.makeText(this, "Invalid hex format", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sends the byte array via UART.
     */
    private fun sendMessage(byteArray: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                uartOutput?.write(byteArray)
                uartOutput?.flush()
                Log.d("UART", "Sent hex")
            } catch (e: Exception) {
                Log.e("UART", "Error sending UART data: ${e.message}")
            }
        }
    }
}

/**
 * Composable UI for sending and receiving UART messages.
 */
@Composable
fun MainScreen(
    receivedMessages: List<String>,
    onSendHexClicked: (String) -> Unit
) {
    var hexInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Hex input field
        OutlinedTextField(
            value = hexInput,
            onValueChange = { hexInput = it },
            label = { Text("Enter Hex Values") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        // Button to send entered hex
        Button(
            onClick = {
                onSendHexClicked(hexInput)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Send Hex")
        }

        // Received Messages List
        Text(
            text = "Received Messages:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxHeight().padding(top = 8.dp)) {
            items(receivedMessages.size) { index ->
                Text(text = receivedMessages[index])
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GPIOcontrolTheme {
        MainScreen(
            receivedMessages = listOf("0x28·0xBB·0x66·0x29", "0x28·0xAA·0x55·0x29"),
            onSendHexClicked = {}
        )
    }
}

/**
 * Parses a hex input string (e.g., "0x23·0x34") into a byte array.
 * Returns null if the input is not valid.
 */
fun parseHexInput(input: String): ByteArray? {
    val hexStrings = input.trim().split("·")
    val byteList = mutableListOf<Byte>()

    for (hex in hexStrings) {
        try {
            // Remove leading "0x" and convert to byte
            val byteValue = hex.removePrefix("0x").toInt(16).toByte()
            byteList.add(byteValue)
        } catch (e: NumberFormatException) {
            return null // Return null if any value is not a valid hex byte
        }
    }

    return byteList.toByteArray()
}
