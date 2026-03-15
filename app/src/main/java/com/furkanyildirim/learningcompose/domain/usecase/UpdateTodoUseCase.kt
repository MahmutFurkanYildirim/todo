package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        val updated = todo.copy(updatedAt = System.currentTimeMillis())
        repository.updateTodo(updated)
        if (updated.firebaseId.isNotEmpty()) {
            runCatching { remoteRepository.updateTodo(updated) }
        }
    }
}
