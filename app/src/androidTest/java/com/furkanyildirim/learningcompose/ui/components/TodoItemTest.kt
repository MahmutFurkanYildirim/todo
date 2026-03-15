package com.furkanyildirim.learningcompose.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Todo
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun todoItem_showsTitleAndMeta() {
        val todo = Todo(
            id = 1,
            title = "UI Test Todo",
            category = Category.OTHER.name
        )

        composeRule.setContent {
            MaterialTheme {
                TodoItem(
                    todo = todo,
                    onToggleComplete = {},
                    onDelete = {}
                )
            }
        }

        composeRule.onNodeWithText("UI Test Todo").assertExists()
        composeRule.onNodeWithText("Orta - Diger - Tarih yok").assertExists()
    }

    @Test
    fun todoItem_toggleCallsCallback() {
        var toggled = false
        val todo = Todo(
            id = 2,
            title = "Toggle Todo",
            isCompleted = false,
            category = Category.WORK.name
        )

        composeRule.setContent {
            MaterialTheme {
                TodoItem(
                    todo = todo,
                    onToggleComplete = { toggled = true },
                    onDelete = {}
                )
            }
        }

        composeRule.onAllNodes(isToggleable()).assertCountEquals(1)
        composeRule.onAllNodes(isToggleable()).onFirst().performClick()
        assertTrue(toggled)
    }

    @Test
    fun todoItem_exposesStateAndDeleteSemantics() {
        val todo = Todo(
            id = 3,
            title = "Delete Me",
            isCompleted = true,
            category = Category.PERSONAL.name
        )

        composeRule.setContent {
            MaterialTheme {
                TodoItem(
                    todo = todo,
                    onToggleComplete = {},
                    onDelete = {}
                )
            }
        }

        composeRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Tamamlandi"
            )
        ).assertExists()
        composeRule.onNodeWithContentDescription("Delete Me gorevini sil").assertIsDisplayed()
    }
}
