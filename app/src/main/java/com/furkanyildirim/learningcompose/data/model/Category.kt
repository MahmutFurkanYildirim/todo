package com.furkanyildirim.learningcompose.data.model

import androidx.compose.ui.graphics.Color

enum class Category(
    val title: String,
    val color: Color
) {
    WORK("Work", Color(0xFF00F5FF)),
    PERSONAL("Personal", Color(0xFF4D7CFF)),
    SHOPPING("Shopping", Color(0xFFFFB800)),
    HEALTH("Health", Color(0xFFFF2BD6)),
    OTHER("Other", Color(0xFF8A9BC8))
}
