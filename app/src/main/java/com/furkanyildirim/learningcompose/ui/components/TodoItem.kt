package com.furkanyildirim.learningcompose.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.ui.common.displayName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItem(
    todo: Todo,
    onToggleComplete: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    onTogglePin: (Todo) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val completedState = stringResource(R.string.state_completed)
    val pendingState = stringResource(R.string.state_pending)
    val cardShape = RoundedCornerShape(18.dp)
    val cardColor by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "cardColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "textColor"
    )
    val category = try {
        Category.valueOf(todo.category)
    } catch (e: Exception) {
        Category.OTHER
    }
    val priority = runCatching { Priority.valueOf(todo.priority) }.getOrDefault(Priority.MEDIUM)

    val dismissState = rememberSwipeToDismissBoxState()
    val showDeleteBackground =
        dismissState.targetValue == SwipeToDismissBoxValue.EndToStart ||
            dismissState.currentValue == SwipeToDismissBoxValue.EndToStart


    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(todo)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            if (showDeleteBackground) {
                                listOf(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.65f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                                )
                            }
                        ),
                        shape = cardShape
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.content_delete),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = cardShape
                )
                .semantics(mergeDescendants = true) {}
                .clickable { onClick() },
            shape = cardShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(category.color)
                )

                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleComplete(todo) },
                    modifier = Modifier.semantics {
                        stateDescription = if (todo.isCompleted) completedState else pendingState
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = todo.title,
                        textDecoration = if (todo.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        },
                        color = textColor
                    )
                    val dueText = todo.dueDate?.let { formatDueDate(it) }
                        ?: stringResource(R.string.label_no_date)
                    Text(
                        text = stringResource(
                            R.string.item_meta_format,
                            "${priority.displayName()} - ${category.displayName()}",
                            dueText
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                    if (todo.project.isNotBlank() || todo.tags.isNotBlank()) {
                        val tagsText = todo.tags
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .joinToString(" ") { "#$it" }
                        val secondaryMeta = buildString {
                            if (todo.project.isNotBlank()) {
                                append(todo.project)
                            }
                            if (tagsText.isNotBlank()) {
                                if (isNotEmpty()) append(" | ")
                                append(tagsText)
                            }
                        }
                        Text(
                            text = secondaryMeta,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                IconButton(onClick = { onTogglePin(todo) }) {
                    Icon(
                        imageVector = if (todo.isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (todo.isPinned) {
                            stringResource(R.string.content_unpin_todo)
                        } else {
                            stringResource(R.string.content_pin_todo)
                        },
                        tint = if (todo.isPinned) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                IconButton(onClick = { onDelete(todo) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.content_delete_todo_fmt, todo.title),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

private fun formatDueDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
