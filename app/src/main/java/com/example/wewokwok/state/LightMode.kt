package com.example.wewokwok.state

/**
 * Visual themes derived from ambient light levels (lux).
 */
enum class LightMode(val label: String, val icon: String) {
    BRIGHT(label = "Bright Mode", icon = "☀️"),
    DIM(label = "Dim Mode", icon = "⛅"),
    ECLIPSE(label = "Eclipse Mode", icon = "🌒")
}
