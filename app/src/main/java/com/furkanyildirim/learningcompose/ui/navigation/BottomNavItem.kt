package com.furkanyildirim.learningcompose.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timer
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.furkanyildirim.learningcompose.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object All : BottomNavItem(
        route = "all",
        titleRes = R.string.nav_all,
        icon = Icons.AutoMirrored.Filled.List
    )

    object Completed : BottomNavItem(
        route = "completed",
        titleRes = R.string.nav_completed,
        icon = Icons.Default.Check
    )

    object Today : BottomNavItem(
        route = "today",
        titleRes = R.string.nav_today,
        icon = Icons.Default.DateRange
    )

    object Focus : BottomNavItem(
        route = "focus",
        titleRes = R.string.nav_focus,
        icon = Icons.Default.Timer
    )
}
