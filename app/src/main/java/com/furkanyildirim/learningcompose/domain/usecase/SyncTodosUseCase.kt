package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.data.preferences.UserPreferences
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncTodosUseCase @Inject constructor(
    private val repository: LocalTodoRepository,
    private val remoteRepository: RemoteTodoRepository,
    private val userPreferences: UserPreferences
) {
    val remoteTodos: Flow<List<Todo>> = remoteRepository.todos

    suspend fun mergeRemoteAndPushPending(remoteTodos: List<Todo>): SyncRunReport {
        var totalOperations = 0
        var failedOperations = 0
        var retriesUsed = 0
        var lastErrorMessage = ""

        totalOperations += 1
        val authResult = withRetry(maxAttempts = 3) {
            remoteRepository.ensureAuthenticated()
        }
        retriesUsed += authResult.attemptsBeyondFirst
        if (!authResult.success) {
            failedOperations += 1
            lastErrorMessage = authResult.error?.message.orEmpty()
        }

        var pendingDeleteIds = userPreferences.getPendingRemoteDeleteIds()
        pendingDeleteIds.forEach { firebaseId ->
            totalOperations += 1
            val deleteResult = withRetry(maxAttempts = 3) {
                remoteRepository.ensureAuthenticated()
                remoteRepository.deleteTodo(firebaseId)
            }
            retriesUsed += deleteResult.attemptsBeyondFirst
            if (deleteResult.success) {
                userPreferences.removePendingRemoteDeleteId(firebaseId)
            } else {
                failedOperations += 1
                lastErrorMessage = deleteResult.error?.message.orEmpty()
            }
        }
        pendingDeleteIds = userPreferences.getPendingRemoteDeleteIds()

        val filteredRemoteTodos = remoteTodos.filterNot { pendingDeleteIds.contains(it.firebaseId) }
        val localWinners = repository.mergeRemoteTodos(filteredRemoteTodos)
        localWinners.forEach { localWinner ->
            totalOperations += 1
            val result = withRetry(maxAttempts = 3) {
                remoteRepository.updateTodo(localWinner)
            }
            retriesUsed += result.attemptsBeyondFirst
            if (!result.success) {
                failedOperations += 1
                lastErrorMessage = result.error?.message.orEmpty()
            }
        }
        repository.pendingTodos().forEach { localTodo ->
            totalOperations += 1
            val result = withRetry(maxAttempts = 3) {
                remoteRepository.addTodo(localTodo)
            }
            retriesUsed += result.attemptsBeyondFirst
            if (result.success) {
                val firebaseId = result.value.orEmpty()
                if (firebaseId.isNotBlank()) {
                    repository.updateTodo(localTodo.copy(firebaseId = firebaseId))
                } else {
                    failedOperations += 1
                    lastErrorMessage = "empty_firebase_id"
                }
            } else {
                failedOperations += 1
                lastErrorMessage = result.error?.message.orEmpty()
            }
        }

        return SyncRunReport(
            totalOperations = totalOperations,
            failedOperations = failedOperations,
            retriesUsed = retriesUsed,
            lastErrorMessage = lastErrorMessage
        )
    }

    private suspend fun <T> withRetry(
        maxAttempts: Int,
        block: suspend () -> T
    ): RetryResult<T> {
        var attempts = 0
        var delayMs = 400L
        var lastError: Throwable? = null

        while (attempts < maxAttempts) {
            attempts++
            val attemptResult = runCatching { block() }
            if (attemptResult.isSuccess) {
                return RetryResult(
                    success = true,
                    value = attemptResult.getOrNull(),
                    attemptsBeyondFirst = (attempts - 1).coerceAtLeast(0),
                    error = null
                )
            }
            lastError = attemptResult.exceptionOrNull()
            if (attempts < maxAttempts) {
                delay(delayMs)
                delayMs = (delayMs * 2L).coerceAtMost(3_000L)
            }
        }

        return RetryResult(
            success = false,
            value = null,
            attemptsBeyondFirst = (attempts - 1).coerceAtLeast(0),
            error = lastError
        )
    }

    private data class RetryResult<T>(
        val success: Boolean,
        val value: T?,
        val attemptsBeyondFirst: Int,
        val error: Throwable?
    )
}
