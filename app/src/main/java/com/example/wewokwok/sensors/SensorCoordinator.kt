package com.example.wewokwok.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * SensorCoordinator manages system sensors (Accelerometer and Light Sensor).
 * Handles null checks for hardware availability, registers and unregisters listeners safely,
 * and passes raw readings to registered callbacks.
 */
class SensorCoordinator(context: Context) : SensorEventListener {

    private val sensorManager =
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

    val hasAccelerometer: Boolean = accelerometer != null
    val hasLightSensor: Boolean = lightSensor != null

    var onAccelerometerData: ((x: Float, y: Float, z: Float) -> Unit)? = null
    var onLightData: ((lux: Float) -> Unit)? = null

    private var isRegistered = false

    /**
     * Safely registers listeners for available sensors.
     * Prevents duplicate registrations.
     */
    fun register() {
        if (isRegistered || sensorManager == null) return

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        isRegistered = true
    }

    /**
     * Safely unregisters all sensor listeners.
     */
    fun unregister() {
        if (!isRegistered || sensorManager == null) return

        sensorManager.unregisterListener(this)
        isRegistered = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values.getOrNull(0) ?: 0f
                val y = event.values.getOrNull(1) ?: 0f
                val z = event.values.getOrNull(2) ?: 0f
                onAccelerometerData?.invoke(x, y, z)
            }
            Sensor.TYPE_LIGHT -> {
                val lux = event.values.getOrNull(0) ?: 0f
                onLightData?.invoke(lux)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op for current use case
    }
}
