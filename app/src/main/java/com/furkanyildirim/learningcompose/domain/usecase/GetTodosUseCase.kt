package com.furkanyildirim.learningcompose.domain.usecase

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodosUseCase @Inject constructor(
    private val repository: LocalTodoRepository
) {
    operator fun invoke(): Flow<List<Todo>> = repository.todos
}
