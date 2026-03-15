package com.furkanyildirim.learningcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.FocusSessionDayStat
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.data.model.SyncTelemetryState
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.data.preferences.UserPreferences
import com.furkanyildirim.learningcompose.domain.usecase.TodoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoUseCases: TodoUseCases,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Room is source of truth.
    val todos: StateFlow<List<Todo>> = todoUseCases.getTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _recentSearches = MutableStateFlow(userPreferences.getRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()
    private val _focusWorkMinutes = MutableStateFlow(userPreferences.getFocusWorkMinutes())
    val focusWorkMinutes: StateFlow<Int> = _focusWorkMinutes.asStateFlow()
    private val _focusBreakMinutes = MutableStateFlow(userPreferences.getFocusBreakMinutes())
    val focusBreakMinutes: StateFlow<Int> = _focusBreakMinutes.asStateFlow()
    private val _focusAutoStartBreak = MutableStateFlow(userPreferences.isFocusAutoStartBreakEnabled())
    val focusAutoStartBreak: StateFlow<Boolean> = _focusAutoStartBreak.asStateFlow()
    private val _focusAutoStartWork = MutableStateFlow(userPreferences.isFocusAutoStartWorkEnabled())
    val focusAutoStartWork: StateFlow<Boolean> = _focusAutoStartWork.asStateFlow()
    private val _focusSessionHistory = MutableStateFlow(userPreferences.getFocusSessionHistory())
    val focusSessionHistory: StateFlow<List<FocusSessionDayStat>> = _focusSessionHistory.asStateFlow()
    private val _syncTelemetry = MutableStateFlow(userPreferences.getSyncTelemetryState())
    val syncTelemetry: StateFlow<SyncTelemetryState> = _syncTelemetry.asStateFlow()
    private var latestRemoteTodos: List<Todo> = emptyList()
    private val syncMutex = Mutex()

    init {
        observeRemoteAndMerge()
        startPeriodicSync()
    }

    private fun observeRemoteAndMerge() {
        viewModelScope.launch {
            todoUseCases.syncTodos.remoteTodos.collect { remoteTodos ->
                latestRemoteTodos = remoteTodos
                runSync(remoteTodos)
            }
        }
    }

    private fun startPeriodicSync() {
        viewModelScope.launch {
            while (true) {
                delay(SYNC_INTERVAL_MS)
                runSync(latestRemoteTodos)
            }
        }
    }

    private suspend fun runSync(remoteTodos: List<Todo>) {
        syncMutex.withLock {
            runCatching {
                todoUseCases.syncTodos.mergeRemoteAndPushPending(remoteTodos)
            }.onSuccess { report ->
                if (report.failedOperations == 0) {
                    userPreferences.recordSyncSuccess(report.retriesUsed)
                } else {
                    userPreferences.recordSyncFailure(
                        error = report.lastErrorMessage.ifBlank {
                            "operations_failed:${report.failedOperations}"
                        },
                        retriesUsed = report.retriesUsed
                    )
                }
                _syncTelemetry.update { userPreferences.getSyncTelemetryState() }
            }.onFailure { throwable ->
                userPreferences.recordSyncFailure(
                    error = throwable.message ?: "sync_failed",
                    retriesUsed = 0
                )
                _syncTelemetry.update { userPreferences.getSyncTelemetryState() }
            }
        }
    }

    fun addTodo(
        title: String,
        priority: Priority = Priority.MEDIUM,
        category: Category = Category.OTHER,
        repeatIntervalDays: Int = 0,
        dueDate: Long? = null,
        project: String = "",
        tags: String = "",
        repeatRule: String = RepeatRule.NONE,
        isPinned: Boolean = false
    ) {
        viewModelScope.launch {
            todoUseCases.addTodo(
                title = title,
                priority = priority,
                category = category,
                repeatIntervalDays = repeatIntervalDays,
                dueDate = dueDate,
                project = project,
                tags = tags,
                repeatRule = repeatRule,
                isPinned = isPinned
            )
        }
    }

    fun addTodoWithId(todo: Todo) {
        viewModelScope.launch {
            todoUseCases.addTodoWithId(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoUseCases.deleteTodo(todo)
        }
    }

    fun doneTodo(todo: Todo) {
        viewModelScope.launch {
            todoUseCases.toggleTodoCompletion(todo)
        }
    }

    fun updateTodoTitle(todo: Todo, newTitle: String) {
        viewModelScope.launch {
            todoUseCases.updateTodo(todo.copy(title = newTitle))
        }
    }

    fun updateTodoDueDate(todo: Todo, dueDate: Long?) {
        viewModelScope.launch {
            todoUseCases.updateTodo(todo.copy(dueDate = dueDate))
        }
    }

    fun updateTodoCategory(todo: Todo, category: Category) {
        viewModelScope.launch {
            todoUseCases.updateTodo(todo.copy(category = category.name))
        }
    }

    fun updateTodoPriority(todo: Todo, priority: Priority) {
        viewModelScope.launch {
            todoUseCases.updateTodo(todo.copy(priority = priority.name))
        }
    }

    fun updateTodoRepeatInterval(todo: Todo, repeatIntervalDays: Int) {
        viewModelScope.launch {
            todoUseCases.updateTodo(
                todo.copy(
                    repeatIntervalDays = repeatIntervalDays.coerceAtLeast(0),
                    repeatRule = RepeatRule.NONE
                )
            )
        }
    }

    fun updateTodoRepeatRule(todo: Todo, repeatRule: String) {
        viewModelScope.launch {
            todoUseCases.updateTodo(
                todo.copy(
                    repeatRule = repeatRule,
                    repeatIntervalDays = 0
                )
            )
        }
    }

    fun togglePinned(todo: Todo) {
        viewModelScope.launch {
            todoUseCases.updateTodo(todo.copy(isPinned = !todo.isPinned))
        }
    }

    fun rememberSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return

        userPreferences.saveRecentSearch(trimmed)
        _recentSearches.update { userPreferences.getRecentSearches() }
    }

    fun clearRecentSearches() {
        userPreferences.clearRecentSearches()
        _recentSearches.update { emptyList() }
    }

    fun setFocusWorkMinutes(minutes: Int) {
        userPreferences.setFocusWorkMinutes(minutes)
        _focusWorkMinutes.update { userPreferences.getFocusWorkMinutes() }
    }

    fun setFocusBreakMinutes(minutes: Int) {
        userPreferences.setFocusBreakMinutes(minutes)
        _focusBreakMinutes.update { userPreferences.getFocusBreakMinutes() }
    }

    fun setFocusAutoStartBreak(enabled: Boolean) {
        userPreferences.setFocusAutoStartBreakEnabled(enabled)
        _focusAutoStartBreak.update { enabled }
    }

    fun setFocusAutoStartWork(enabled: Boolean) {
        userPreferences.setFocusAutoStartWorkEnabled(enabled)
        _focusAutoStartWork.update { enabled }
    }

    fun recordFocusSession(focusedMinutes: Int, completedAt: Long = System.currentTimeMillis()) {
        userPreferences.recordFocusSession(
            focusedMinutes = focusedMinutes,
            completedAt = completedAt
        )
        _focusSessionHistory.update { userPreferences.getFocusSessionHistory() }
    }

    companion object {
        private const val SYNC_INTERVAL_MS = 30_000L
    }
}
