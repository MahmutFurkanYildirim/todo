package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    selectedLanguage: String,
    signedInEmail: String?,
    recentSearchCount: Int,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onClearRecentSearches: () -> Unit,
    onResetOnboarding: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NeonBarContainer(position = NeonBarPosition.Top) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.title_settings),
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SettingsSectionCard(
                        title = stringResource(R.string.label_dark_theme),
                        subtitle = stringResource(R.string.action_manage_theme)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.label_dark_theme),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    stringResource(R.string.desc_theme_persistence),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = onThemeChange
                            )
                        }
                    }
                }

                item {
                    SettingsSectionCard(
                        title = stringResource(R.string.label_language),
                        subtitle = stringResource(R.string.action_manage_language)
                    ) {
                        Text(
                            stringResource(R.string.desc_language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedLanguage == "tr",
                                onClick = { onLanguageChange("tr") },
                                label = { Text(stringResource(R.string.language_tr)) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                            FilterChip(
                                selected = selectedLanguage == "en",
                                onClick = { onLanguageChange("en") },
                                label = { Text(stringResource(R.string.language_en)) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                        }
                    }
                }

                item {
                    SettingsSectionCard(
                        title = stringResource(R.string.label_account),
                        subtitle = stringResource(R.string.action_manage_account)
                    ) {
                        Text(
                            text = signedInEmail ?: stringResource(R.string.auth_not_signed_in),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (signedInEmail.isNullOrBlank()) {
                            Button(
                                onClick = onGoogleSignIn,
                                modifier = Modifier.fillMaxWidth(),
                                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                            ) {
                                Text(stringResource(R.string.auth_google_sign_in))
                            }
                        }
                        if (!signedInEmail.isNullOrBlank()) {
                            Button(
                                onClick = onSignOut,
                                modifier = Modifier.fillMaxWidth(),
                                shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                            ) {
                                Text(stringResource(R.string.auth_sign_out))
                            }
                        }
                    }
                }

                item {
                    SettingsSectionCard(
                        title = stringResource(R.string.title_dashboard),
                        subtitle = stringResource(R.string.action_manage_history)
                    ) {
                        Button(
                            onClick = onClearRecentSearches,
                            modifier = Modifier.fillMaxWidth(),
                            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                        ) {
                            Text(stringResource(R.string.action_clear_recent_searches_fmt, recentSearchCount))
                        }
                    }
                }

                item {
                    SettingsSectionCard(
                        title = stringResource(R.string.title_onboarding_welcome),
                        subtitle = stringResource(R.string.action_manage_onboarding)
                    ) {
                        Text(
                            text = stringResource(R.string.action_reset_onboarding),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onResetOnboarding,
                            modifier = Modifier.fillMaxWidth(),
                            shape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                        ) {
                            Text(stringResource(R.string.action_reset_onboarding))
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {}
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String,
    content: @androidx.compose.runtime.Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                content()
            }
        )
    }
}
