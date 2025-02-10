package com.example.gpiocontrol

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gpiocontrol.ui.theme.GPIOcontrolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ZtlApi.ZtlManager
import android.serialport.SerialPort
import java.io.InputStream
import java.io.OutputStream
import java.io.File

class MainActivity : ComponentActivity() {

    // UART objects
    private var serialPort: SerialPort? = null
    private var uartOutput: OutputStream? = null
    private var uartInput: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GPIOcontrolTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Initialize UART before starting GPIO control
        initUart()

        // Start toggling GPIO on startup
        toggleGpioOnStart()
    }

    /**
     * Initializes the UART port. Adjust the serial port path and baud rate according to your device.
     */
    private fun initUart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Open the serial port with all necessary parameters
                serialPort = SerialPort(
                    File("/dev/ttyS1"),  // Adjust this if your device uses a different port
                    19200,               // Baud rate
                    0,                   // Flags (usually 0)
                    8,                   // Data bits (usually 8)
                    1,                   // Stop bits (1 or 2)
                    0                    // Parity (0 = None, 1 = Odd, 2 = Even)
                )

                uartOutput = serialPort?.outputStream
                uartInput = serialPort?.inputStream

                Log.d("UART", "UART initialized on /dev/ttyS1 at 19200 baud")

            } catch (e: Exception) {
                Log.e("UART", "Error initializing UART: ${e.message}")
            }
        }
    }


    /**
     * Continuously reads data from the UART input stream and logs it.
     */
    private suspend fun readUartData() {
        val buffer = ByteArray(1024)
        while (true) {
            try {
                val bytesRead = uartInput?.read(buffer) ?: -1
                if (bytesRead > 0) {
                    val received = String(buffer, 0, bytesRead)
                    Log.d("UART", "Received: $received")
                }
            } catch (e: Exception) {
                Log.e("UART", "Error reading UART: ${e.message}")
                break
            }
        }
    }

    /**
     * Toggles GPIO ports on and off in an infinite loop. When setting the GPIOs high,
     * it sends the string "28 ,BB 66 , 29" over UART.
     */
    private fun toggleGpioOnStart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Initialize ZtlManager with the Context
                val ztlManager = ZtlManager.GetInstance()
                ztlManager.setContext(applicationContext)

                if (ztlManager == null) {
                    Log.e("GPIO", "ZtlManager instance is null!")
                    return@launch
                }

                val gpioPorts = listOf("GPIO7_A5", "GPIO7_A6", "GPIO7_B3", "GPIO7_B4", "GPIO7_B5")
                while (true) { // Infinite loop for continuous blinking
                    // Turn GPIOs ON
                    for (port in gpioPorts) {
                        ztlManager.setGpioValue(port, 1) // Turn LED ON
                        Log.d("GPIO", "$port set HIGH")
                    }
                    // When GPIOs are high, send "fcuk me" over UART.
                    try {
                        uartOutput?.write(byteArrayOf(0x28.toByte(), 0xBB.toByte(), 0x66.toByte(), 0x29.toByte()))
                        uartOutput?.flush()
                        Log.d("UART", "Sent: fcuk me")
                    } catch (e: Exception) {
                        Log.e("UART", "Error sending UART data: ${e.message}")
                    }
                    delay(1000) // Wait 1 second

                    // Turn GPIOs OFF
                    for (port in gpioPorts) {
                        ztlManager.setGpioValue(port, 0) // Turn LED OFF
                        Log.d("GPIO", "$port set LOW")
                    }
                    delay(1000) // Wait 1 second
                }
            } catch (e: Exception) {
                Log.e("GPIO", "Error controlling GPIO: ${e.message}")
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        GPIOcontrolTheme {
            Greeting("GPIO Control")
        }
    }
}
