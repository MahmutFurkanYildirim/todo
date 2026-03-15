package com.furkanyildirim.learningcompose.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import org.junit.Rule
import org.junit.Test

class TodoScreenAccessibilityTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun recentSearchesHeading_hasHeadingSemantics() {
        composeRule.setContent {
            MaterialTheme {
                RecentSearchesHeading()
            }
        }

        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Heading, Unit)
        ).assertExists()
    }
}
