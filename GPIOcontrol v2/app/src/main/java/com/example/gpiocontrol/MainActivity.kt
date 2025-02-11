package com.example.gpiocontrol

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
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
                    onSendClicked = { message ->
                        sendMessage(message)
                    }
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
     * Sends a message via UART.
     */
    private fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Convert the input string to bytes and send
                val command = message.toByteArray()
                uartOutput?.write(command)
                uartOutput?.flush()
                Log.d("UART", "Sent: $message")
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
    onSendClicked: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Input Field
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text("Enter Command") },
            modifier = Modifier.fillMaxWidth()
        )

        // Send Button
        Button(
            onClick = {
                if (textFieldValue.text.isNotEmpty()) {
                    onSendClicked(textFieldValue.text)
                    textFieldValue = TextFieldValue("") // Clear the input field
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Send")
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
            receivedMessages = listOf("0x28 0xBB 0x66 0x29", "0x28 0xAA 0x55 0x29"),
            onSendClicked = {}
        )
    }
}
