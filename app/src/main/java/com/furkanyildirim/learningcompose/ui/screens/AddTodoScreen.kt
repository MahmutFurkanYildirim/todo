package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.ui.common.displayName
import com.furkanyildirim.learningcompose.ui.components.TodoDatePickerDialog
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import com.furkanyildirim.learningcompose.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(Category.OTHER) }
    var projectName by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }
    var selectedRepeatIntervalDays by remember { mutableStateOf(0) }
    var selectedRepeatRule by remember { mutableStateOf(RepeatRule.NONE) }
    var selectedDueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAdvancedOptions by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.sheet_add_new_todo),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { innerPadding ->
        CyberpunkBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SectionCard {
                        Text(
                            text = stringResource(R.string.title_primary_details),
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text(stringResource(R.string.label_todo_title)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                            colors = outlinedCyberColors()
                        )
                    }
                }

                item {
                    SectionCard {
                        Text(stringResource(R.string.label_priority), style = cyberSectionTitleStyle())
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                CyberChip(
                                    selected = selectedPriority == Priority.LOW,
                                    onClick = { selectedPriority = Priority.LOW },
                                    label = Priority.LOW.displayName()
                                )
                            }
                            item {
                                CyberChip(
                                    selected = selectedPriority == Priority.MEDIUM,
                                    onClick = { selectedPriority = Priority.MEDIUM },
                                    label = Priority.MEDIUM.displayName()
                                )
                            }
                            item {
                                CyberChip(
                                    selected = selectedPriority == Priority.HIGH,
                                    onClick = { selectedPriority = Priority.HIGH },
                                    label = Priority.HIGH.displayName()
                                )
                            }
                        }
                    }
                }

                item {
                    SectionCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.title_advanced_details),
                                style = cyberSectionTitleStyle()
                            )
                            IconButton(onClick = { showAdvancedOptions = !showAdvancedOptions }) {
                                Icon(
                                    imageVector = if (showAdvancedOptions) {
                                        Icons.Default.ExpandLess
                                    } else {
                                        Icons.Default.ExpandMore
                                    },
                                    contentDescription = if (showAdvancedOptions) {
                                        stringResource(R.string.action_hide_details)
                                    } else {
                                        stringResource(R.string.action_show_details)
                                    },
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        FilterChip(
                            selected = selectedDueDate != null,
                            onClick = { showDatePicker = true },
                            label = {
                                Text(
                                    selectedDueDate?.let { formatAddDate(it) }
                                        ?: stringResource(R.string.label_add_due_date)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = stringResource(R.string.content_open_due_date_picker)
                                )
                            },
                            trailingIcon = {
                                if (selectedDueDate != null) {
                                    IconButton(onClick = { selectedDueDate = null }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.action_clear_date)
                                        )
                                    }
                                }
                            },
                            colors = cyberChipColors(),
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                        if (showAdvancedOptions) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(stringResource(R.string.label_category), style = cyberSectionTitleStyle())
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(Category.entries, key = { it.name }) { category ->
                                    CyberChip(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        label = category.displayName()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.label_repeat), style = cyberSectionTitleStyle())
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    CyberChip(
                                        selected = selectedRepeatIntervalDays == 0,
                                        onClick = {
                                            selectedRepeatIntervalDays = 0
                                            selectedRepeatRule = RepeatRule.NONE
                                        },
                                        label = stringResource(R.string.repeat_none)
                                    )
                                }
                                item {
                                    CyberChip(
                                        selected = selectedRepeatIntervalDays == 1,
                                        onClick = {
                                            selectedRepeatIntervalDays = 1
                                            selectedRepeatRule = RepeatRule.NONE
                                        },
                                        label = stringResource(R.string.repeat_daily)
                                    )
                                }
                                item {
                                    CyberChip(
                                        selected = selectedRepeatIntervalDays == 7,
                                        onClick = {
                                            selectedRepeatIntervalDays = 7
                                            selectedRepeatRule = RepeatRule.NONE
                                        },
                                        label = stringResource(R.string.repeat_weekly)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.label_smart_repeat), style = cyberSectionTitleStyle())
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    CyberChip(
                                        selected = selectedRepeatRule == RepeatRule.NONE,
                                        onClick = { selectedRepeatRule = RepeatRule.NONE },
                                        label = stringResource(R.string.repeat_rule_none)
                                    )
                                }
                                item {
                                    CyberChip(
                                        selected = selectedRepeatRule == RepeatRule.WEEKDAYS,
                                        onClick = {
                                            selectedRepeatRule = RepeatRule.WEEKDAYS
                                            selectedRepeatIntervalDays = 0
                                        },
                                        label = stringResource(R.string.repeat_rule_weekdays)
                                    )
                                }
                                item {
                                    CyberChip(
                                        selected = selectedRepeatRule == RepeatRule.MONDAY,
                                        onClick = {
                                            selectedRepeatRule = RepeatRule.MONDAY
                                            selectedRepeatIntervalDays = 0
                                        },
                                        label = stringResource(R.string.repeat_rule_monday)
                                    )
                                }
                                item {
                                    CyberChip(
                                        selected = selectedRepeatRule == RepeatRule.LAST_FRIDAY,
                                        onClick = {
                                            selectedRepeatRule = RepeatRule.LAST_FRIDAY
                                            selectedRepeatIntervalDays = 0
                                        },
                                        label = stringResource(R.string.repeat_rule_last_friday)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = projectName,
                                onValueChange = { projectName = it },
                                label = { Text(stringResource(R.string.label_project_hint)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                                colors = outlinedCyberColors()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = tagInput,
                                onValueChange = { tagInput = it },
                                label = { Text(stringResource(R.string.label_tags_hint)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp),
                                colors = outlinedCyberColors(),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            val normalized = normalizeTag(tagInput)
                                            if (normalized.isNotBlank() && !selectedTags.contains(normalized)) {
                                                selectedTags.add(normalized)
                                            }
                                            tagInput = ""
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(R.string.content_add_tag),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            )
                            if (selectedTags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(selectedTags, key = { it }) { tag ->
                                        FilterChip(
                                            selected = true,
                                            onClick = { selectedTags.remove(tag) },
                                            label = { Text("#$tag") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = stringResource(R.string.content_remove_tag)
                                                )
                                            },
                                            colors = cyberChipColors()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.addTodo(
                                    title = inputText,
                                    priority = selectedPriority,
                                    category = selectedCategory,
                                    project = projectName.trim(),
                                    tags = selectedTags.joinToString(","),
                                    repeatIntervalDays = selectedRepeatIntervalDays,
                                    dueDate = selectedDueDate,
                                    repeatRule = selectedRepeatRule
                                )
                                onBackClick()
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.button_add),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.7.sp
                            )
                        )
                    }
                }
            }

            if (showDatePicker) {
                TodoDatePickerDialog(
                    initialDate = selectedDueDate,
                    onDateSelected = { selectedDueDate = it },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content
        )
    }
}

@Composable
private fun cyberSectionTitleStyle() = MaterialTheme.typography.labelLarge.copy(
    letterSpacing = 1.sp,
    fontWeight = FontWeight.SemiBold,
    color = MaterialTheme.colorScheme.secondary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun cyberChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
    selectedLabelColor = MaterialTheme.colorScheme.primary,
    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
    selectedTrailingIconColor = MaterialTheme.colorScheme.primary,
    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CyberChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = cyberChipColors()
    )
}

@Composable
private fun outlinedCyberColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedLabelColor = MaterialTheme.colorScheme.secondary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)

private fun normalizeTag(raw: String): String {
    return raw.trim().lowercase().removePrefix("#").replace(" ", "")
}

private fun formatAddDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
