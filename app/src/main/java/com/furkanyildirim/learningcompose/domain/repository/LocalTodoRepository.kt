package com.furkanyildirim.learningcompose.domain.repository

import com.furkanyildirim.learningcompose.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface LocalTodoRepository {
    val todos: Flow<List<Todo>>
    suspend fun addTodo(todo: Todo): Int
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun clearAllTodos()
    suspend fun pendingTodos(): List<Todo>
    suspend fun mergeRemoteTodos(remoteTodos: List<Todo>): List<Todo>
}
