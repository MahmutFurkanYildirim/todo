package com.furkanyildirim.learningcompose.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

enum class NeonBarPosition {
    Top,
    Bottom
}

@Composable
fun NeonBarContainer(
    position: NeonBarPosition = NeonBarPosition.Top,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "neonBarTransition")
    val sweepProgress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neonSweep"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFF0F1C31),
                        Color(0xFF16213A),
                        Color(0xFF132844)
                    )
                )
            )
            .drawWithContent {
                drawContent()

                val x = size.width * sweepProgress
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x66FF2BD6),
                            Color(0x9900F5FF),
                            Color.Transparent
                        ),
                        start = Offset(x - size.width * 0.35f, 0f),
                        end = Offset(x, size.height)
                    ),
                    blendMode = BlendMode.Screen
                )

                val blendFade = if (position == NeonBarPosition.Top) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33131D31),
                            Color(0xAA101A2A)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xAA101A2A),
                            Color(0x33131D31),
                            Color.Transparent
                        )
                    )
                }

                drawRect(
                    brush = blendFade,
                    blendMode = BlendMode.Multiply
                )
            },
        content = content
    )
}
