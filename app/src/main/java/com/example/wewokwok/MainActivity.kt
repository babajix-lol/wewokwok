package com.example.wewokwok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.wewokwok.sensors.SensorCoordinator
import com.example.wewokwok.state.GameViewModel
import com.example.wewokwok.ui.BalanceGameScreen
import com.example.wewokwok.ui.theme.WewokwokTheme

/**
 * MainActivity sets up the Compose content, initializes the SensorCoordinator,
 * wires sensor data callbacks to the GameViewModel, and launches BalanceGameScreen.
 */
class MainActivity : ComponentActivity() {

    private lateinit var sensorCoordinator: SensorCoordinator
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorCoordinator = SensorCoordinator(this)

        // Connect sensor data listeners to ViewModel
        sensorCoordinator.onAccelerometerData = { ax, ay, _ ->
            viewModel.onAccelerometerChanged(ax, ay)
        }

        sensorCoordinator.onLightData = { lux ->
            viewModel.onLightSensorChanged(lux)
        }

        // Notify ViewModel of hardware availability
        viewModel.onSensorsAvailabilityChecked(
            hasAccelerometer = sensorCoordinator.hasAccelerometer,
            hasLightSensor = sensorCoordinator.hasLightSensor
        )

        setContent {
            WewokwokTheme {
                BalanceGameScreen(
                    viewModel = viewModel,
                    sensorCoordinator = sensorCoordinator
                )
            }
        }
    }
}
