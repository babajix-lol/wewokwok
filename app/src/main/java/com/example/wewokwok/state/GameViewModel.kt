package com.example.wewokwok.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.sqrt

/**
 * ViewModel managing Eclipse Ball game state, sensor reading conversions,
 * motion sensitivity, boundary constraints, and ambient light mode switching.
 */
class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var maxRadius: Float = 300f
    private val ballRadius: Float = 24f
    private val sensitivity: Float = 2.5f

    /**
     * Initializes sensor availability flags and fallback status messages.
     */
    fun onSensorsAvailabilityChecked(hasAccelerometer: Boolean, hasLightSensor: Boolean) {
        val statusMsg = when {
            !hasAccelerometer -> "Accelerometer sensor missing! Motion controls are unavailable on this device."
            !hasLightSensor -> "Ambient light sensor not supported on this device. Using default bright theme."
            else -> null
        }

        _uiState.update { current ->
            current.copy(
                isAccelerometerAvailable = hasAccelerometer,
                isLightSensorAvailable = hasLightSensor,
                statusMessage = statusMsg
            )
        }
    }

    /**
     * Updates ball position from accelerometer tilt (X and Y values).
     * Applies boundary clamping to keep ball within the arena.
     */
    fun onAccelerometerChanged(ax: Float, ay: Float) {
        if (!_uiState.value.isAccelerometerAvailable) return

        _uiState.update { current ->
            // On standard portrait Android device:
            // Tilting right: ax < 0 -> ball moves right (+X)
            // Tilting forward/down: ay > 0 -> ball moves down (+Y)
            val deltaX = -ax * sensitivity
            val deltaY = ay * sensitivity

            val rawX = current.ballX + deltaX
            val rawY = current.ballY + deltaY

            // Calculate distance from center (0,0)
            val distance = sqrt(rawX * rawX + rawY * rawY)
            val allowedRadius = (maxRadius - ballRadius).coerceAtLeast(10f)

            // Clamp inside circle
            val (clampedX, clampedY) = if (distance > allowedRadius && distance > 0) {
                val scale = allowedRadius / distance
                Pair(rawX * scale, rawY * scale)
            } else {
                Pair(rawX, rawY)
            }

            val currentDistance = sqrt(clampedX * clampedX + clampedY * clampedY)
            val nearCenter = currentDistance < 30f
            val newScore = if (nearCenter) current.score + 1 else current.score

            current.copy(
                ballX = clampedX,
                ballY = clampedY,
                distanceFromCenter = currentDistance,
                isNearCenter = nearCenter,
                score = newScore
            )
        }
    }

    /**
     * Converts ambient light lux readings into stable LightMode states.
     * Thresholds prevent rapid flickering.
     */
    fun onLightSensorChanged(lux: Float) {
        val currentMode = _uiState.value.lightMode

        // Apply hysteresis thresholding
        val newMode = when {
            lux < 15f -> LightMode.ECLIPSE
            lux in 15f..80f -> {
                if (currentMode == LightMode.ECLIPSE && lux < 25f) LightMode.ECLIPSE
                else if (currentMode == LightMode.BRIGHT && lux > 70f) LightMode.BRIGHT
                else LightMode.DIM
            }
            else -> LightMode.BRIGHT
        }

        _uiState.update { current ->
            current.copy(
                luxValue = lux,
                lightMode = newMode
            )
        }
    }

    /**
     * Updates arena bounds when Compose layout dimensions change.
     */
    fun onArenaSizeChanged(width: Float, height: Float) {
        val minDim = minOf(width, height)
        maxRadius = (minDim / 2f) * 0.85f
    }

    /**
     * Resets ball position and score.
     */
    fun resetGame() {
        _uiState.update { current ->
            current.copy(
                ballX = 0f,
                ballY = 0f,
                score = 0,
                distanceFromCenter = 0f,
                isNearCenter = true
            )
        }
    }
}
