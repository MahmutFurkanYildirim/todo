package com.furkanyildirim.learningcompose.domain.repository

import com.furkanyildirim.learningcompose.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface RemoteTodoRepository {
    val todos: Flow<List<Todo>>
    suspend fun addTodo(todo: Todo): String
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(firebaseId: String)
    suspend fun ensureAuthenticated()
}
