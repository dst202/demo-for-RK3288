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
        // Use coroutines instead of raw threads
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Initialize ZtlManager with the Context
                val ztlManager = ZtlManager.GetInstance()
                ztlManager.setContext(applicationContext) // Set the context as per the instructions

                if (ztlManager == null) {
                    Log.e("GPIO", "ZtlManager instance is null!")
                    return@launch
                }

                // String-based GPIO name
                val gpioPorts = listOf("GPIO7_A5", "GPIO7_A6", "GPIO7_B3", "GPIO7_B4", "GPIO7_B5")
                for (port in gpioPorts) {
                    // Call the method without expecting a return value
                    ztlManager.setGpioValue(port, 1) // Assuming this is a void method

                    Log.d("GPIO", "$port set HIGH successfully")
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
