package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import javax.inject.Inject

class AddTodoWithIdUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        val localId = repository.addTodo(todo.copy(firebaseId = "", updatedAt = System.currentTimeMillis()))
        val inserted = todo.copy(id = localId, firebaseId = "", updatedAt = System.currentTimeMillis())

        runCatching { remoteRepository.addTodo(inserted) }
            .onSuccess { firebaseId ->
                repository.updateTodo(inserted.copy(firebaseId = firebaseId))
            }
    }
}
