package com.example.gpiocontrol

import android.os.Bundle
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
import android.util.Log
import ZtlApi.ZtlManager
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val gpioPin = 221 // GPIO number

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

        // **Turn on GPIO on startup**
        toggleGpioOnStart()
    }

    private fun toggleGpioOnStart() {
        Thread {
            try {
                // Use `GetInstance()` to get the singleton instance of ZtlManager
                val ztlManager = ZtlManager.GetInstance() // FIXED: Use GetInstance()

                // Set GPIO pin HIGH
                val gpioType = gpioPin
                val isInput = false // Set as output
                val isHigh = true // Set high

                val result = ztlManager.setGpioValue(gpioType, isInput, isHigh)

                if (result == 1) {
                    Log.d("GPIO", "GPIO $gpioType set HIGH successfully")
                } else {
                    Log.e("GPIO", "Failed to set GPIO $gpioType. Error code: $result")
                }
            } catch (e: Exception) {
                Log.e("GPIO", "Error controlling GPIO: ${e.message}")
            }
        }.start()
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun executeShellCommand(command: String): String {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        process.waitFor() // Wait for command to complete
        return "Done"
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
