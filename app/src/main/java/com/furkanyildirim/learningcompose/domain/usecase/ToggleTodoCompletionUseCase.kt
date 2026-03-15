package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import java.util.Calendar
import javax.inject.Inject

class ToggleTodoCompletionUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        val willComplete = !todo.isCompleted
        val updatedTodo = todo.copy(
            isCompleted = willComplete,
            updatedAt = System.currentTimeMillis()
        )
        repository.updateTodo(updatedTodo)
        if (todo.firebaseId.isNotEmpty()) {
            runCatching { remoteRepository.updateTodo(updatedTodo) }
        }

        if (willComplete && (todo.repeatIntervalDays > 0 || todo.repeatRule != RepeatRule.NONE)) {
            createRecurringTodo(todo)
        }
    }

    private suspend fun createRecurringTodo(source: Todo) {
        val base = source.dueDate ?: System.currentTimeMillis()
        val nextDueDate = when {
            source.repeatRule != RepeatRule.NONE -> nextDueDateFromRule(base, source.repeatRule)
            source.repeatIntervalDays > 0 -> base + source.repeatIntervalDays * DAY_MILLIS
            else -> null
        }
        val recurringTodo = source.copy(
            id = 0,
            isCompleted = false,
            dueDate = nextDueDate,
            updatedAt = System.currentTimeMillis(),
            firebaseId = ""
        )
        val localId = repository.addTodo(recurringTodo)
        val inserted = recurringTodo.copy(id = localId)

        runCatching { remoteRepository.addTodo(inserted) }
            .onSuccess { firebaseId ->
                repository.updateTodo(inserted.copy(firebaseId = firebaseId))
            }
    }

    private fun nextDueDateFromRule(baseTimestamp: Long, rule: String): Long? {
        val base = Calendar.getInstance().apply { timeInMillis = baseTimestamp }
        return when (rule) {
            RepeatRule.WEEKDAYS -> nextWeekday(base)
            RepeatRule.MONDAY -> nextWeekdayOf(base, Calendar.MONDAY)
            RepeatRule.LAST_FRIDAY -> nextMonthLastFriday(base)
            else -> null
        }
    }

    private fun nextWeekday(base: Calendar): Long {
        val cal = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun nextWeekdayOf(base: Calendar, dayOfWeek: Int): Long {
        val cal = (base.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        while (cal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun nextMonthLastFriday(base: Calendar): Long {
        val cal = (base.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return cal.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
