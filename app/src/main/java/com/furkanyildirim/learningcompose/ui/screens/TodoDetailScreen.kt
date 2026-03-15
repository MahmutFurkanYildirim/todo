package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.ui.components.CategoryPickerDialog
import com.furkanyildirim.learningcompose.ui.components.PriorityPickerDialog
import com.furkanyildirim.learningcompose.ui.components.RepeatPickerDialog
import com.furkanyildirim.learningcompose.ui.common.displayName
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todo: Todo,
    onBackClick: () -> Unit,
    onUpdateTitle: (Todo, String) -> Unit,
    onUpdateCategory: (Todo, Category) -> Unit,
    onUpdatePriority: (Todo, Priority) -> Unit,
    onUpdateRepeatInterval: (Todo, Int) -> Unit,
    onUpdateRepeatRule: (Todo, String) -> Unit,
    onTogglePinned: (Todo) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(todo.title) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showPriorityPicker by remember { mutableStateOf(false) }
    var showRepeatPicker by remember { mutableStateOf(false) }
    var showSmartRepeatPicker by remember { mutableStateOf(false) }

    val category = runCatching { Category.valueOf(todo.category) }.getOrDefault(Category.OTHER)
    val priority = runCatching { Priority.valueOf(todo.priority) }.getOrDefault(Priority.MEDIUM)

    Scaffold(
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_todo_detail),
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (isEditing && editedTitle.isNotBlank()) onUpdateTitle(todo, editedTitle)
                                isEditing = !isEditing
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = if (isEditing) {
                                    stringResource(R.string.content_save)
                                } else {
                                    stringResource(R.string.content_edit)
                                }
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        CyberpunkBackground(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text(stringResource(R.string.label_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                } else {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusRing(
                        progress = if (todo.isCompleted) 1f else 0.35f,
                        modifier = Modifier.size(24.dp),
                        color = if (todo.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = if (todo.isCompleted) {
                            stringResource(R.string.status_completed)
                        } else {
                            stringResource(R.string.status_pending)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (todo.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showCategoryPicker = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = stringResource(R.string.content_open_category_picker),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(category.color)
                    )
                    Text(
                        text = category.displayName(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showPriorityPicker = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_priority),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = priority.displayName(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showRepeatPicker = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = stringResource(R.string.content_open_repeat_picker),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.label_repeat),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = repeatIntervalLabel(todo.repeatIntervalDays),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { showSmartRepeatPicker = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_smart_repeat),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = smartRuleLabel(todo.repeatRule),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onTogglePinned(todo) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (todo.isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (todo.isPinned) {
                            stringResource(R.string.content_unpin_todo)
                        } else {
                            stringResource(R.string.content_pin_todo)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (showCategoryPicker) {
                CategoryPickerDialog(
                    selectedCategory = category,
                    onCategorySelected = { onUpdateCategory(todo, it) },
                    onDismiss = { showCategoryPicker = false }
                )
            }

            if (showPriorityPicker) {
                PriorityPickerDialog(
                    selectedPriority = priority,
                    onPrioritySelected = { onUpdatePriority(todo, it) },
                    onDismiss = { showPriorityPicker = false }
                )
            }

            if (showRepeatPicker) {
                RepeatPickerDialog(
                    selectedDays = todo.repeatIntervalDays,
                    onRepeatSelected = { onUpdateRepeatInterval(todo, it) },
                    onDismiss = { showRepeatPicker = false }
                )
            }

            if (showSmartRepeatPicker) {
                SmartRepeatRuleDialog(
                    selectedRule = todo.repeatRule,
                    onDismiss = { showSmartRepeatPicker = false },
                    onRuleSelected = {
                        onUpdateRepeatRule(todo, it)
                        showSmartRepeatPicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun repeatIntervalLabel(days: Int): String {
    return when (days) {
        1 -> stringResource(R.string.repeat_daily)
        7 -> stringResource(R.string.repeat_weekly)
        30 -> stringResource(R.string.repeat_monthly)
        else -> stringResource(R.string.repeat_off)
    }
}

@Composable
private fun smartRuleLabel(rule: String): String {
    return when (rule) {
        RepeatRule.WEEKDAYS -> stringResource(R.string.repeat_rule_weekdays)
        RepeatRule.MONDAY -> stringResource(R.string.repeat_rule_monday)
        RepeatRule.LAST_FRIDAY -> stringResource(R.string.repeat_rule_last_friday)
        else -> stringResource(R.string.repeat_rule_none)
    }
}

@Composable
private fun StatusRing(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    trackColor: Color
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = stroke
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            style = stroke
        )
    }
}

@Composable
private fun SmartRepeatRuleDialog(
    selectedRule: String,
    onDismiss: () -> Unit,
    onRuleSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.label_smart_repeat)) },
        text = {
            Column {
                listOf(
                    RepeatRule.NONE,
                    RepeatRule.WEEKDAYS,
                    RepeatRule.MONDAY,
                    RepeatRule.LAST_FRIDAY
                ).forEach { rule ->
                    TextButton(
                        onClick = { onRuleSelected(rule) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = smartRuleLabel(rule),
                            color = if (rule == selectedRule) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        }
    )
}
