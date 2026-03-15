package com.furkanyildirim.learningcompose.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithContent

@Composable
fun CyberpunkBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF111D32),
                        Color(0xFF0F1A2F),
                        Color(0xFF12152A),
                        Color(0xFF0F1A2F),
                        Color(0xFF111D32)
                    )
                )
            )
            .drawWithContent {
                drawContent()

                // Top and bottom bridge bands to soften header/footer transition.
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x5516213A),
                            Color.Transparent
                        )
                    ),
                    blendMode = BlendMode.SrcOver
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x5516213A)
                        )
                    ),
                    blendMode = BlendMode.SrcOver
                )

                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3300F5FF),
                            Color.Transparent
                        ),
                        radius = size.maxDimension * 0.9f,
                        center = center
                    ),
                    blendMode = BlendMode.Screen
                )

                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x22FF2BD6),
                            Color.Transparent,
                            Color(0x2200F5FF)
                        )
                    ),
                    blendMode = BlendMode.Plus
                )
            },
        content = content
    )
}

