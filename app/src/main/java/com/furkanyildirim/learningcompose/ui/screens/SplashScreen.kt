package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val introScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.86f,
        animationSpec = tween(700),
        label = "introScale"
    )
    val infinite = rememberInfiniteTransition(label = "splash")
    val corePulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )
    val scanOffset by infinite.animateFloat(
        initialValue = -420f,
        targetValue = 420f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanOffset"
    )
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )
    val glow by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2100)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = cyberpunkSplashBrush())
            .drawWithCache {
                onDrawWithContent {
                    drawContent()

                    val spacing = 56.dp.toPx()
                    var x = -scanOffset
                    while (x < size.width + spacing) {
                        drawLine(
                            color = Color(0x2215E6FF),
                            start = Offset(x, 0f),
                            end = Offset(x - size.height * 0.22f, size.height),
                            strokeWidth = 1f
                        )
                        x += spacing
                    }

                    val horizontalSpacing = 44.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = Color(0x1400F5FF),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += horizontalSpacing
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(corePulse)
                .alpha(glow)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(230.dp)
                .graphicsLayer { rotationZ = ringRotation }
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xAA00F5FF),
                            Color.Transparent,
                            Color(0xAAFF2BD6),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .width(260.dp)
                .height(4.dp)
                .offset(y = scanOffset.dp / 24)
                .alpha(0.22f)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xAA00F5FF), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(vertical = 1.dp)
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(550)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(550)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(74.dp)
                        .scale(introScale)
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFB7D6FF),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0x4400F5FF), Color.Transparent)
                                ),
                                blendMode = BlendMode.Screen
                            )
                        }
                )
            }
        }
    }
}

private fun cyberpunkSplashBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF03060E),
            Color(0xFF08122A),
            Color(0xFF0E1130),
            Color(0xFF0B1E35),
            Color(0xFF040913)
        )
    )
}
