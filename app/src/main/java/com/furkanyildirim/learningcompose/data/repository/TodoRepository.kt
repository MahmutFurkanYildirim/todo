package com.furkanyildirim.learningcompose.data.repository

import com.furkanyildirim.learningcompose.data.local.TodoDao
import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.LocalTodoRepository
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) : LocalTodoRepository {
    override val todos: Flow<List<Todo>> = todoDao.todos()

    override suspend fun addTodo(todo: Todo): Int = todoDao.addTodo(todo).toInt()

    override suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    override suspend fun deleteTodo(todo: Todo) {
        if (todo.id > 0) {
            todoDao.deleteTodoById(todo.id)
        } else {
            todoDao.deleteTodo(todo)
        }
        if (todo.firebaseId.isNotBlank()) {
            todoDao.deleteTodoByFirebaseId(todo.firebaseId)
        }
    }

    override suspend fun clearAllTodos() = todoDao.clearAll()

    override suspend fun pendingTodos(): List<Todo> = todoDao.pendingTodos()

    override suspend fun mergeRemoteTodos(remoteTodos: List<Todo>): List<Todo> {
        val localWinnersToPush = mutableListOf<Todo>()
        val remoteByFirebase = remoteTodos
            .filter { it.firebaseId.isNotBlank() }
            .associateBy { it.firebaseId }
        val locals = todoDao.allTodosOnce()
        val localByFirebase = locals
            .filter { it.firebaseId.isNotBlank() }
            .associateBy { it.firebaseId }

        remoteByFirebase.values.forEach { remote ->
            val local = localByFirebase[remote.firebaseId]
            if (local == null) {
                val pendingMatch = locals.firstOrNull { localTodo ->
                    localTodo.firebaseId.isBlank() && isSameLogicalTodo(localTodo, remote)
                }
                if (pendingMatch != null) {
                    todoDao.updateTodo(
                        pendingMatch.copy(
                            firebaseId = remote.firebaseId,
                            updatedAt = remote.updatedAt
                        )
                    )
                } else {
                    todoDao.addTodo(remote.copy(id = 0))
                }
            } else {
                val resolved = resolveConflict(local = local, remote = remote.copy(id = local.id))
                if (resolved == local) {
                    localWinnersToPush.add(local)
                } else {
                    todoDao.updateTodo(resolved)
                }
            }
        }

        locals
            .filter { it.firebaseId.isNotBlank() && !remoteByFirebase.containsKey(it.firebaseId) }
            .forEach { todoDao.deleteTodoById(it.id) }

        return localWinnersToPush
    }

    private fun isSameLogicalTodo(local: Todo, remote: Todo): Boolean {
        return local.title == remote.title &&
            local.isCompleted == remote.isCompleted &&
            local.dueDate == remote.dueDate &&
            local.priority == remote.priority &&
            local.category == remote.category &&
            local.project == remote.project &&
            local.tags == remote.tags &&
            local.repeatIntervalDays == remote.repeatIntervalDays &&
            local.repeatRule == remote.repeatRule &&
            local.isPinned == remote.isPinned &&
            local.updatedAt == remote.updatedAt
    }

    private fun resolveConflict(local: Todo, remote: Todo): Todo {
        return when {
            remote.updatedAt > local.updatedAt -> remote
            remote.updatedAt < local.updatedAt -> local
            remote.isCompleted && !local.isCompleted -> remote
            local.isCompleted && !remote.isCompleted -> local
            (remote.dueDate ?: Long.MAX_VALUE) < (local.dueDate ?: Long.MAX_VALUE) -> remote
            else -> local
        }
    }
}
