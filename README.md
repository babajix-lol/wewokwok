# Eclipse Ball 🌒

A multi-sensor Android balance game built with Kotlin and Jetpack Compose.

---

## Overview

**Eclipse Ball** is a polished mini-game where players navigate a ball into concentric target circles using physical device motion, while ambient light levels dynamically shift the app's visual atmosphere and theme.

---

## Sensors Used

1. **Accelerometer** (`Sensor.TYPE_ACCELEROMETER`)
   - **Category**: Motion Sensor
   - **UI/UX Impact**: Controls physical ball movement (X and Y displacement) inside the arena. Tilting the device moves the ball across concentric target rings towards the bullseye.

2. **Ambient Light Sensor** (`Sensor.TYPE_LIGHT`)
   - **Category**: Environment Sensor
   - **UI/UX Impact**: Dynamically changes visual theme and UI atmosphere based on ambient lighting:
     - **Bright Mode** (`> 80 lx`): Vibrant daylight palette with high-contrast target rings.
     - **Dim Mode** (`15 - 80 lx`): Warm twilight tones with muted accents.
     - **Eclipse Mode** (`< 15 lx`): Deep cosmic dark theme with neon cyan and violet glowing halos, glowing ball aura, and vibrant neon arena rings.

---

## Lifecycle Handling

- **Lifecycle Aware**: Uses `LocalLifecycleOwner` and `DisposableEffect` to listen for lifecycle events (`ON_RESUME` / `ON_PAUSE`).
- **Energy & Memory Efficiency**: Sensor listeners are registered on `ON_RESUME` and unregistered on `ON_PAUSE` or screen disposal to prevent battery drain.
- **Duplicate Protection**: `SensorCoordinator` guards against duplicate registrations.

---

## Hardware Fallback & Availability

- **Accelerometer Unavailable**: Blocks motion gameplay safely and displays a prominent warning banner explaining that motion controls are unsupported.
- **Light Sensor Unavailable**: Defaults gracefully to **Bright Mode** theme while keeping motion controls fully playable, and displays a status message stating: *"Ambient light sensor not supported on this device"*.

---

## Project Structure

```text
app/src/main/java/com/example/wewokwok/
├── MainActivity.kt
├── sensors/
│   └── SensorCoordinator.kt
├── state/
│   ├── GameUiState.kt
│   ├── GameViewModel.kt
│   └── LightMode.kt
└── ui/
    ├── BalanceGameScreen.kt
    └── components/
        └── SensorStatusBanner.kt
```

---

## How to Run

1. Open the project in **Android Studio**.
2. Connect a physical Android device (recommended for testing real accelerometer and ambient light sensor readings) or run on an Android Emulator with Virtual Sensors enabled in Extended Controls.
3. Select the `app` run configuration and click **Run** (`Shift + F10`).
