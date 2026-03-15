package com.furkanyildirim.learningcompose.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority

@Composable
fun Category.displayName(): String {
    return when (this) {
        Category.WORK -> stringResource(R.string.category_work)
        Category.PERSONAL -> stringResource(R.string.category_personal)
        Category.SHOPPING -> stringResource(R.string.category_shopping)
        Category.HEALTH -> stringResource(R.string.category_health)
        Category.OTHER -> stringResource(R.string.category_other)
    }
}

@Composable
fun Priority.displayName(): String {
    return when (this) {
        Priority.LOW -> stringResource(R.string.priority_low)
        Priority.MEDIUM -> stringResource(R.string.priority_medium)
        Priority.HIGH -> stringResource(R.string.priority_high)
    }
}
