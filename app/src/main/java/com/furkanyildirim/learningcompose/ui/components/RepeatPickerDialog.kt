package com.furkanyildirim.learningcompose.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.furkanyildirim.learningcompose.R

private data class RepeatOption(val label: String, val days: Int)

@Composable
fun RepeatPickerDialog(
    selectedDays: Int,
    onRepeatSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        RepeatOption(stringResource(R.string.repeat_none), 0),
        RepeatOption(stringResource(R.string.repeat_daily), 1),
        RepeatOption(stringResource(R.string.repeat_weekly), 7),
        RepeatOption(stringResource(R.string.repeat_monthly), 30)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_repeat),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                options.forEach { option ->
                    FilterChip(
                        selected = selectedDays == option.days,
                        onClick = {
                            onRepeatSelected(option.days)
                            onDismiss()
                        },
                        label = { Text(option.label) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(R.string.action_close))
                }
            }
        }
    }
}
