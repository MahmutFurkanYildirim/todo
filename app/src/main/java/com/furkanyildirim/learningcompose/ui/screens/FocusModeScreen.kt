package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class FocusPhase {
    WORK, BREAK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(
    workDurationMinutes: Int,
    breakDurationMinutes: Int,
    autoStartBreak: Boolean,
    autoStartWork: Boolean,
    onWorkDurationChange: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    onAutoStartBreakChange: (Boolean) -> Unit,
    onAutoStartWorkChange: (Boolean) -> Unit,
    onFocusSessionCompleted: (Int, Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workDurationSeconds = (workDurationMinutes.coerceIn(10, 60) * 60L)
    val breakDurationSeconds = (breakDurationMinutes.coerceIn(3, 30) * 60L)

    var phase by rememberSaveable { mutableStateOf(FocusPhase.WORK.name) }
    var remainingSeconds by rememberSaveable { mutableLongStateOf(workDurationSeconds) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var completedWorkSessions by rememberSaveable { mutableIntStateOf(0) }
    var totalFocusedMinutes by rememberSaveable { mutableIntStateOf(0) }

    val currentPhase = remember(phase) { FocusPhase.valueOf(phase) }
    val phaseTotalSeconds = if (currentPhase == FocusPhase.WORK) workDurationSeconds else breakDurationSeconds
    val progress = ((phaseTotalSeconds - remainingSeconds).toFloat() / phaseTotalSeconds.toFloat())
        .coerceIn(0f, 1f)
    val timerScale by animateFloatAsState(
        targetValue = if (isRunning) 1.03f else 1f,
        animationSpec = tween(durationMillis = 420),
        label = "timerScale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "focusPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val buttonGlow by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonGlow"
    )

    LaunchedEffect(workDurationSeconds, breakDurationSeconds, currentPhase) {
        val phaseDuration = if (currentPhase == FocusPhase.WORK) workDurationSeconds else breakDurationSeconds
        if (remainingSeconds > phaseDuration) {
            remainingSeconds = phaseDuration
        }
    }

    LaunchedEffect(isRunning, remainingSeconds, currentPhase) {
        if (!isRunning || remainingSeconds <= 0L) {
            return@LaunchedEffect
        }
        delay(1_000)
        remainingSeconds -= 1
        if (remainingSeconds == 0L) {
            if (currentPhase == FocusPhase.WORK) {
                completedWorkSessions += 1
                totalFocusedMinutes += (workDurationSeconds / 60L).toInt()
                onFocusSessionCompleted((workDurationSeconds / 60L).toInt(), System.currentTimeMillis())
                phase = FocusPhase.BREAK.name
                remainingSeconds = breakDurationSeconds
                isRunning = autoStartBreak
            } else {
                phase = FocusPhase.WORK.name
                remainingSeconds = workDurationSeconds
                isRunning = autoStartWork
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_focus_mode),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_back),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (currentPhase == FocusPhase.WORK) {
                                    stringResource(R.string.focus_phase_work)
                                } else {
                                    stringResource(R.string.focus_phase_break)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Box(
                                modifier = Modifier.graphicsLayer(
                                    scaleX = timerScale,
                                    scaleY = timerScale,
                                    alpha = if (isRunning) pulseAlpha else 1f
                                )
                            ) {
                                Text(
                                    text = formatTime(remainingSeconds),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                                color = if (isRunning) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                            Text(
                                text = stringResource(
                                    R.string.focus_progress_percent,
                                    (progress * 100f).roundToInt()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { isRunning = !isRunning },
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    shadowElevation = if (isRunning) 18f * buttonGlow else 0f
                                    alpha = if (isRunning) 0.9f + (0.1f * buttonGlow) else 1f
                                },
                            shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.78f + (0.22f * buttonGlow))
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                if (isRunning) stringResource(R.string.action_pause)
                                else stringResource(R.string.action_start)
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                isRunning = false
                                remainingSeconds =
                                    if (currentPhase == FocusPhase.WORK) workDurationSeconds else breakDurationSeconds
                            },
                            modifier = Modifier.weight(1f),
                            shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                        ) {
                            Text(stringResource(R.string.action_reset))
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.focus_session_metrics),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            MetricRow(
                                label = stringResource(R.string.focus_completed_sessions),
                                value = completedWorkSessions.toString()
                            )
                            MetricRow(
                                label = stringResource(R.string.focus_total_minutes),
                                value = totalFocusedMinutes.toString()
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.focus_timer_settings),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.focus_work_duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(15, 25, 45).forEach { value ->
                                    FilterChip(
                                        selected = workDurationMinutes == value,
                                        onClick = { onWorkDurationChange(value) },
                                        label = { Text(stringResource(R.string.focus_minutes_chip_fmt, value)) },
                                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Text(
                                text = stringResource(R.string.focus_break_duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(5, 10, 15).forEach { value ->
                                    FilterChip(
                                        selected = breakDurationMinutes == value,
                                        onClick = { onBreakDurationChange(value) },
                                        label = { Text(stringResource(R.string.focus_minutes_chip_fmt, value)) },
                                        shape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f),
                                            selectedLabelColor = MaterialTheme.colorScheme.secondary,
                                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            SwitchRow(
                                label = stringResource(R.string.focus_auto_start_break),
                                checked = autoStartBreak,
                                onCheckedChange = onAutoStartBreakChange
                            )
                            SwitchRow(
                                label = stringResource(R.string.focus_auto_start_work),
                                checked = autoStartWork,
                                onCheckedChange = onAutoStartWorkChange
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60L
    val remaining = seconds % 60L
    return "%02d:%02d".format(minutes, remaining)
}
