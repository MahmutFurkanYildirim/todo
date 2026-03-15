package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.FocusSessionDayStat
import com.furkanyildirim.learningcompose.data.model.SyncTelemetryState
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.ui.common.displayName
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    todos: List<Todo>,
    focusSessionHistory: List<FocusSessionDayStat>,
    syncTelemetry: SyncTelemetryState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val total = todos.size
    val completed = todos.count { it.isCompleted }
    val overdue = todos.count { !it.isCompleted && it.dueDate != null && it.dueDate < now }
    val completionRate = if (total == 0) 0 else ((completed * 100f) / total).toInt()
    val topCategory = Category.entries.maxByOrNull { category ->
        todos.count { it.category == category.name }
    }?.displayName() ?: "-"

    val openTodos = todos.filterNot { it.isCompleted }
    val overdueRatio = if (openTodos.isEmpty()) 0 else ((overdue * 100f) / openTodos.size).toInt()
    val weeklyCompletion = weeklyCompletionCounts(todos, now)
    val totalFocusSessions = focusSessionHistory.sumOf { it.sessions }
    val totalFocusMinutes = focusSessionHistory.sumOf { it.minutes }
    val weeklyFocusMinutes = weeklyFocusMinutes(focusSessionHistory, now)
    val focusDistribution = Category.entries
        .map { category -> category.displayName() to todos.count { it.category == category.name } }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }

    Scaffold(
        modifier = modifier,
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.title_dashboard),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { innerPadding ->
        CyberpunkBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SectionTitle(text = stringResource(R.string.title_stats_overview))
                }
                item {
                    SummaryCard(
                        completionRate = completionRate,
                        completed = completed,
                        total = total,
                        overdue = overdue
                    )
                }

                item {
                    MetricCard(title = stringResource(R.string.title_stats_productivity)) {
                        StatCardContent(
                            title = stringResource(R.string.stat_total_tasks),
                            value = total.toString()
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_completed_tasks),
                            value = completed.toString()
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_overdue_tasks),
                            value = overdue.toString()
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_top_category),
                            value = topCategory
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_overdue_ratio),
                            value = "%$overdueRatio"
                        )
                    }
                }

                item {
                    SectionTitle(text = stringResource(R.string.title_stats_focus))
                }
                item {
                    MetricCard(title = stringResource(R.string.stat_weekly_completion)) {
                        weeklyCompletion.forEach { (label, count) ->
                            MetricBarRow(
                                label = label,
                                value = count,
                                max = weeklyCompletion.maxOfOrNull { it.second } ?: 1
                            )
                        }
                    }
                }

                item {
                    MetricCard(title = stringResource(R.string.stat_weekly_focus_minutes)) {
                        StatCardContent(
                            title = stringResource(R.string.stat_focus_sessions),
                            value = totalFocusSessions.toString()
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_focus_minutes),
                            value = totalFocusMinutes.toString()
                        )
                        weeklyFocusMinutes.forEach { (label, minutes) ->
                            MetricBarRow(
                                label = label,
                                value = minutes,
                                max = weeklyFocusMinutes.maxOfOrNull { it.second } ?: 1
                            )
                        }
                    }
                }

                item {
                    MetricCard(title = stringResource(R.string.stat_focus_distribution)) {
                        if (focusDistribution.isEmpty()) {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            val max = focusDistribution.maxOf { it.second }
                            focusDistribution.forEach { (label, count) ->
                                MetricBarRow(label = label, value = count, max = max)
                            }
                        }
                    }
                }

                item {
                    SectionTitle(text = stringResource(R.string.section_sync_health))
                }
                item {
                    MetricCard(title = stringResource(R.string.section_sync_health)) {
                        StatCardContent(
                            title = stringResource(R.string.stat_sync_last_success),
                            value = formatSyncTime(syncTelemetry.lastSuccessAt)
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_sync_failures),
                            value = "${syncTelemetry.totalFailureCount}/${syncTelemetry.consecutiveFailureCount}"
                        )
                        StatCardContent(
                            title = stringResource(R.string.stat_sync_retries),
                            value = syncTelemetry.totalRetryCount.toString()
                        )
                        if (syncTelemetry.lastError.isNotBlank()) {
                            Text(
                                text = syncTelemetry.lastError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    completionRate: Int,
    completed: Int,
    total: Int,
    overdue: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$completionRate% ${if (total == 0) "-" else stringResource(R.string.stat_completion_rate)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            LinearProgressIndicator(
                progress = { (completionRate / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(
                    R.string.stat_summary_detail_fmt,
                    completed,
                    total,
                    overdue
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun StatCardContent(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MetricBarRow(
    label: String,
    value: Int,
    max: Int
) {
    val fraction = if (max <= 0) 0f else (value.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

private fun weeklyCompletionCounts(todos: List<Todo>, now: Long): List<Pair<String, Int>> {
    val startOfToday = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return (6 downTo 0).map { offset ->
        val dayStart = (startOfToday.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -offset)
        }
        val dayEnd = (dayStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
            add(Calendar.MILLISECOND, -1)
        }
        val completedCount = todos.count { todo ->
            todo.isCompleted && todo.updatedAt in dayStart.timeInMillis..dayEnd.timeInMillis
        }
        val label = dayStart.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, java.util.Locale.getDefault())
            ?: dayStart.get(Calendar.DAY_OF_WEEK).toString()
        label to completedCount
    }
}

private fun weeklyFocusMinutes(
    history: List<FocusSessionDayStat>,
    now: Long
): List<Pair<String, Int>> {
    val historyMap = history.associateBy { it.dayKey }
    val startOfToday = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val keyFormat = SimpleDateFormat("yyyyMMdd", Locale.US)

    return (6 downTo 0).map { offset ->
        val dayCalendar = (startOfToday.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -offset)
        }
        val dayKey = keyFormat.format(dayCalendar.time)
        val label = dayCalendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.SHORT,
            Locale.getDefault()
        ) ?: dayCalendar.get(Calendar.DAY_OF_WEEK).toString()
        label to (historyMap[dayKey]?.minutes ?: 0)
    }
}

private fun formatSyncTime(timestamp: Long): String {
    if (timestamp <= 0L) return "-"
    val formatter = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    return formatter.format(timestamp)
}
