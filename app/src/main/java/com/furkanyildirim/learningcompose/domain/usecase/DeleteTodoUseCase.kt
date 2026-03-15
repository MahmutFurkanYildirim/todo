package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.data.preferences.UserPreferences
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(todo: Todo) {
        if (todo.firebaseId.isNotEmpty()) {
            userPreferences.addPendingRemoteDeleteId(todo.firebaseId)
        }
        repository.deleteTodo(todo)
        if (todo.firebaseId.isNotEmpty()) {
            runCatching {
                remoteRepository.ensureAuthenticated()
                remoteRepository.deleteTodo(todo.firebaseId)
            }
                .onSuccess { userPreferences.removePendingRemoteDeleteId(todo.firebaseId) }
        }
    }
}
