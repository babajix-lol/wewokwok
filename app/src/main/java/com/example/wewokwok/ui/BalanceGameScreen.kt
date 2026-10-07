package com.example.wewokwok.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wewokwok.sensors.SensorCoordinator
import com.example.wewokwok.state.GameUiState
import com.example.wewokwok.state.GameViewModel
import com.example.wewokwok.state.LightMode
import com.example.wewokwok.ui.components.SensorStatusBanner

/**
 * Main game screen for Eclipse Ball.
 * Handles lifecycle observation for sensors and renders dynamic themes based on LightMode.
 */
@Composable
fun BalanceGameScreen(
    viewModel: GameViewModel,
    sensorCoordinator: SensorCoordinator,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Lifecycle-aware sensor listener registration
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sensorCoordinator.register()
                Lifecycle.Event.ON_PAUSE -> sensorCoordinator.unregister()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorCoordinator.unregister()
        }
    }

    // Dynamic background colors depending on ambient light mode
    val bgStartColor by animateColorAsState(
        targetValue = when (uiState.lightMode) {
            LightMode.BRIGHT -> Color(0xFFE0F7FA)
            LightMode.DIM -> Color(0xFF263238)
            LightMode.ECLIPSE -> Color(0xFF0D0221)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "bgStart"
    )

    val bgEndColor by animateColorAsState(
        targetValue = when (uiState.lightMode) {
            LightMode.BRIGHT -> Color(0xFFFFF9C4)
            LightMode.DIM -> Color(0xFF102A43)
            LightMode.ECLIPSE -> Color(0xFF02010A)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "bgEnd"
    )

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgStartColor, bgEndColor)))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header section
                GameHeader(
                    uiState = uiState,
                    onResetClicked = { viewModel.resetGame() }
                )

                // Arena section
                GameArena(
                    uiState = uiState,
                    onSizeChanged = { w, h -> viewModel.onArenaSizeChanged(w, h) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Status banner section for fallback hardware messages
                uiState.statusMessage?.let { msg ->
                    SensorStatusBanner(
                        message = msg,
                        isCritical = !uiState.isAccelerometerAvailable
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHeader(
    uiState: GameUiState,
    onResetClicked: () -> Unit
) {
    val textColor = when (uiState.lightMode) {
        LightMode.BRIGHT -> Color(0xFF1A237E)
        LightMode.DIM -> Color(0xFFECEFF1)
        LightMode.ECLIPSE -> Color(0xFF00E5FF)
    }

    val cardBg = when (uiState.lightMode) {
        LightMode.BRIGHT -> Color.White.copy(alpha = 0.85f)
        LightMode.DIM -> Color(0xFF37474F).copy(alpha = 0.85f)
        LightMode.ECLIPSE -> Color(0xFF190033).copy(alpha = 0.85f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ECLIPSE BALL",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.lightMode.icon,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = uiState.lightMode.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "${"%.1f".format(uiState.luxValue)} lx",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textColor.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                // Score Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCORE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${uiState.score}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = textColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                // Reset Button
                Button(
                    onClick = onResetClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (uiState.lightMode) {
                            LightMode.BRIGHT -> Color(0xFF3F51B5)
                            LightMode.DIM -> Color(0xFF78909C)
                            LightMode.ECLIPSE -> Color(0xFFD500F9)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun GameArena(
    uiState: GameUiState,
    onSizeChanged: (width: Float, height: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val outerRingColor by animateColorAsState(
        targetValue = when (uiState.lightMode) {
            LightMode.BRIGHT -> Color(0xFF3949AB)
            LightMode.DIM -> Color(0xFFFFB74D)
            LightMode.ECLIPSE -> Color(0xFF00E5FF)
        },
        animationSpec = tween(durationMillis = 500),
        label = "outerRing"
    )

    val innerRingColor by animateColorAsState(
        targetValue = when (uiState.lightMode) {
            LightMode.BRIGHT -> Color(0xFF5C6BC0)
            LightMode.DIM -> Color(0xFFFF8A65)
            LightMode.ECLIPSE -> Color(0xFFE040FB)
        },
        animationSpec = tween(durationMillis = 500),
        label = "innerRing"
    )

    val centerBullseyeColor by animateColorAsState(
        targetValue = when {
            uiState.isNearCenter -> Color(0xFF00E676)
            uiState.lightMode == LightMode.BRIGHT -> Color(0xFFE53935)
            uiState.lightMode == LightMode.DIM -> Color(0xFFFFD54F)
            else -> Color(0xFF00B0FF)
        },
        animationSpec = tween(durationMillis = 300),
        label = "centerBullseye"
    )

    val ballColor by animateColorAsState(
        targetValue = when (uiState.lightMode) {
            LightMode.BRIGHT -> Color(0xFF1A237E)
            LightMode.DIM -> Color(0xFFECEFF1)
            LightMode.ECLIPSE -> Color(0xFF00E5FF)
        },
        animationSpec = tween(durationMillis = 500),
        label = "ballColor"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Notify ViewModel of dimensions
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    onSizeChanged(widthPx, heightPx)
                },
            contentAlignment = Alignment.Center
        ) {
            val minSize = minOf(this@BoxWithConstraints.maxWidth, this@BoxWithConstraints.maxHeight) * 0.85f

            // Arena concentric target circles
            Canvas(modifier = Modifier.size(minSize)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f

                // Outer boundary circle
                drawCircle(
                    color = outerRingColor.copy(alpha = 0.2f),
                    radius = maxRadius,
                    center = center
                )
                drawCircle(
                    color = outerRingColor,
                    radius = maxRadius,
                    center = center,
                    style = Stroke(width = 6f)
                )

                // Middle target ring
                drawCircle(
                    color = innerRingColor,
                    radius = maxRadius * 0.65f,
                    center = center,
                    style = Stroke(width = 4f)
                )

                // Inner target ring
                drawCircle(
                    color = innerRingColor.copy(alpha = 0.8f),
                    radius = maxRadius * 0.35f,
                    center = center,
                    style = Stroke(width = 3f)
                )

                // Center bullseye
                drawCircle(
                    color = centerBullseyeColor,
                    radius = 24f,
                    center = center
                )
            }

            // Target bullseye indicator badge
            if (uiState.isNearCenter) {
                Text(
                    text = "BULLSEYE!",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }

            // Moving Ball
            val ballSizeDp = 48.dp

            Box(
                modifier = Modifier
                    .size(ballSizeDp)
                    .graphicsLayer {
                        translationX = uiState.ballX
                        translationY = uiState.ballY
                    }
                    .shadow(
                        elevation = if (uiState.lightMode == LightMode.ECLIPSE) 16.dp else 6.dp,
                        shape = CircleShape,
                        spotColor = if (uiState.lightMode == LightMode.ECLIPSE) Color(0xFF00E5FF) else Color.Black
                    )
                    .clip(CircleShape)
                    .background(
                        if (uiState.lightMode == LightMode.ECLIPSE) {
                            Brush.radialGradient(
                                colors = listOf(Color.White, ballColor, Color(0xFF6200EA))
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(Color.White, ballColor)
                            )
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (uiState.lightMode == LightMode.ECLIPSE) Color(0xFF00E5FF) else Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}
