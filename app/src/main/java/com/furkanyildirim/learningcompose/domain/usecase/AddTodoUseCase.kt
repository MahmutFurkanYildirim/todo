package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Category
import com.furkanyildirim.learningcompose.data.model.Priority
import com.furkanyildirim.learningcompose.data.model.RepeatRule
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import javax.inject.Inject

class AddTodoUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository
) {
    suspend operator fun invoke(
        title: String,
        priority: Priority,
        category: Category,
        repeatIntervalDays: Int,
        dueDate: Long?,
        project: String = "",
        tags: String = "",
        repeatRule: String = RepeatRule.NONE,
        isPinned: Boolean = false
    ) {
        val localTodo = Todo(
            title = title,
            priority = priority.name,
            category = category.name,
            project = project,
            tags = tags,
            repeatIntervalDays = repeatIntervalDays,
            repeatRule = repeatRule,
            isPinned = isPinned,
            dueDate = dueDate,
            updatedAt = System.currentTimeMillis()
        )
        val localId = repository.addTodo(localTodo)
        val inserted = localTodo.copy(id = localId)

        runCatching { remoteRepository.addTodo(inserted) }
            .onSuccess { firebaseId ->
                repository.updateTodo(inserted.copy(firebaseId = firebaseId))
            }
    }
}
