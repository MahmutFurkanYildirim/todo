package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.ui.theme.CyberpunkBackground
import com.furkanyildirim.learningcompose.ui.theme.HotMagenta
import com.furkanyildirim.learningcompose.ui.theme.LearningComposeTheme
import com.furkanyildirim.learningcompose.ui.theme.NeonBarContainer
import com.furkanyildirim.learningcompose.ui.theme.NeonBarPosition
import com.furkanyildirim.learningcompose.ui.theme.NeonCyan

private data class OnboardingPage(val title: String, val description: String)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_page_plan_title),
            description = stringResource(R.string.onboarding_page_plan_desc)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_page_offline_title),
            description = stringResource(R.string.onboarding_page_offline_desc)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_page_habit_title),
            description = stringResource(R.string.onboarding_page_habit_desc)
        )
    )
    var pageIndex by remember { mutableIntStateOf(0) }

    OnboardingContent(
        pages = pages,
        pageIndex = pageIndex,
        onNext = {
            if (pageIndex == pages.lastIndex) onFinish() else pageIndex++
        },
        onPrevious = {
            if (pageIndex > 0) pageIndex--
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingContent(
    pages: List<OnboardingPage>,
    pageIndex: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val current = pages[pageIndex]
    val isLastPage = pageIndex == pages.lastIndex
    val cardShape = RoundedCornerShape(26.dp)

    CyberpunkBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NeonBarContainer(
                position = NeonBarPosition.Top,
                modifier = Modifier.height(10.dp)
            ) {}

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.2.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    NeonCyan.copy(alpha = 0.88f),
                                    HotMagenta.copy(alpha = 0.88f)
                                )
                            ),
                            shape = cardShape
                        ),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.title_onboarding_welcome),
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonCyan
                        )
                        Text(
                            text = current.title,
                            modifier = Modifier.semantics { heading() },
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = current.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(pages.size) { index ->
                                val active = index == pageIndex
                                Box(
                                    modifier = Modifier
                                        .weight(if (active) 1.8f else 1f)
                                        .height(8.dp)
                                        .background(
                                            color = if (active) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }

                        Text(
                            text = "${pageIndex + 1}/${pages.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = HotMagenta
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onPrevious,
                                enabled = pageIndex > 0,
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(
                                    width = 1.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            NeonCyan.copy(alpha = 0.9f),
                                            HotMagenta.copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(text = stringResource(R.string.action_back))
                            }

                            Button(
                                onClick = onNext,
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLastPage) HotMagenta else NeonCyan,
                                    contentColor = if (isLastPage) Color(0xFF180010) else Color(0xFF031015)
                                )
                            ) {
                                Text(
                                    text = if (isLastPage) {
                                        stringResource(R.string.action_start)
                                    } else {
                                        stringResource(R.string.action_continue)
                                    },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            NeonBarContainer(
                position = NeonBarPosition.Bottom,
                modifier = Modifier.height(10.dp)
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    LearningComposeTheme {
        OnboardingContent(
            pages = listOf(
                OnboardingPage("Planla", "Gunluk gorevlerini futuristik panelde yonet."),
                OnboardingPage("Offline", "Baglantin olmasa da tum gorevlerin senkron kalir."),
                OnboardingPage("Rutin", "Hedeflerini aliskanliga donustur, ritmi koru.")
            ),
            pageIndex = 1,
            onNext = {},
            onPrevious = {}
        )
    }
}
