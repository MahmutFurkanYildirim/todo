package com.furkanyildirim.learningcompose.viewmodel

import com.furkanyildirim.learningcompose.MainDispatcherRule
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.data.preferences.UserPreferences
import com.furkanyildirim.learningcompose.domain.usecase.AddTodoUseCase
import com.furkanyildirim.learningcompose.domain.usecase.AddTodoWithIdUseCase
import com.furkanyildirim.learningcompose.domain.usecase.DeleteTodoUseCase
import com.furkanyildirim.learningcompose.domain.usecase.GetTodosUseCase
import com.furkanyildirim.learningcompose.domain.usecase.SyncTodosUseCase
import com.furkanyildirim.learningcompose.domain.usecase.TodoUseCases
import com.furkanyildirim.learningcompose.domain.usecase.ToggleTodoCompletionUseCase
import com.furkanyildirim.learningcompose.domain.usecase.UpdateTodoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTodosUseCase: GetTodosUseCase = mock()
    private val addTodoUseCase: AddTodoUseCase = mock()
    private val addTodoWithIdUseCase: AddTodoWithIdUseCase = mock()
    private val deleteTodoUseCase: DeleteTodoUseCase = mock()
    private val toggleTodoCompletionUseCase: ToggleTodoCompletionUseCase = mock()
    private val updateTodoUseCase: UpdateTodoUseCase = mock()
    private val syncTodosUseCase: SyncTodosUseCase = mock()
    private val userPreferences: UserPreferences = mock()

    private fun createViewModel(): TodoViewModel {
        whenever(getTodosUseCase.invoke()).thenReturn(flowOf(emptyList<Todo>()))
        whenever(syncTodosUseCase.remoteTodos).thenReturn(emptyFlow<List<Todo>>())
        whenever(userPreferences.getRecentSearches()).thenReturn(emptyList())

        val useCases = TodoUseCases(
            getTodos = getTodosUseCase,
            addTodo = addTodoUseCase,
            addTodoWithId = addTodoWithIdUseCase,
            deleteTodo = deleteTodoUseCase,
            toggleTodoCompletion = toggleTodoCompletionUseCase,
            updateTodo = updateTodoUseCase,
            syncTodos = syncTodosUseCase
        )

        return TodoViewModel(useCases, userPreferences)
    }

    @Test
    fun addTodo_delegatesToUseCase() = runTest {
        val viewModel = createViewModel()

        viewModel.addTodo("Test Todo", Priority.HIGH, Category.WORK, 7)

        verify(addTodoUseCase).invoke("Test Todo", Priority.HIGH, Category.WORK, 7, null)
    }

    @Test
    fun deleteTodo_delegatesToUseCase() = runTest {
        val viewModel = createViewModel()
        val todo = Todo(id = 3, title = "Delete", firebaseId = "fb_3")

        viewModel.deleteTodo(todo)

        verify(deleteTodoUseCase).invoke(eq(todo))
    }

    @Test
    fun doneTodo_delegatesToToggleUseCase() = runTest {
        val viewModel = createViewModel()
        val todo = Todo(id = 2, title = "Done", isCompleted = false)

        viewModel.doneTodo(todo)

        verify(toggleTodoCompletionUseCase).invoke(eq(todo))
    }

    @Test
    fun updateTodoTitle_delegatesWithUpdatedTodo() = runTest {
        val viewModel = createViewModel()
        val todo = Todo(id = 10, title = "Old")

        viewModel.updateTodoTitle(todo, "New")

        verify(updateTodoUseCase).invoke(eq(todo.copy(title = "New")))
    }

    @Test
    fun rememberSearch_savesWhenLengthIsEnough() = runTest {
        val viewModel = createViewModel()

        viewModel.rememberSearch("compose")

        verify(userPreferences).saveRecentSearch("compose")
        verify(userPreferences).getRecentSearches()
    }

    @Test
    fun rememberSearch_ignoresShortQueries() = runTest {
        val viewModel = createViewModel()

        viewModel.rememberSearch("a")

        verify(userPreferences).getRecentSearches()
        verify(userPreferences, never()).saveRecentSearch(any())
    }
}
