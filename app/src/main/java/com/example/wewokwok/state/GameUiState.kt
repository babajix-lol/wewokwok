package com.example.wewokwok.state

/**
 * Immutable UI state for Eclipse Ball game.
 */
data class GameUiState(
    val ballX: Float = 0f,
    val ballY: Float = 0f,
    val luxValue: Float = 100f,
    val lightMode: LightMode = LightMode.BRIGHT,
    val isAccelerometerAvailable: Boolean = true,
    val isLightSensorAvailable: Boolean = true,
    val statusMessage: String? = null,
    val score: Int = 0,
    val isNearCenter: Boolean = false,
    val distanceFromCenter: Float = 0f
)
