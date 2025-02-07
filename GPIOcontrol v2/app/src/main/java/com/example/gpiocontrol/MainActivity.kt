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
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import ZtlApi.ZtlManager


class MainActivity : ComponentActivity() {
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
                    for (port in gpioPorts) {
                        ztlManager.setGpioValue(port, 1) // Turn LED ON
                        Log.d("GPIO", "$port set HIGH")
                    }
                    delay(1000) // Wait 1 second

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
