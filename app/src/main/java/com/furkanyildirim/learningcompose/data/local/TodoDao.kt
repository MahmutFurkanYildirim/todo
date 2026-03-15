package com.furkanyildirim.learningcompose.data.local

import androidx.room.*
import com.furkanyildirim.learningcompose.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY id DESC")
    fun todos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos")
    suspend fun allTodosOnce(): List<Todo>

    @Query("SELECT * FROM todos WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun todoByFirebaseId(firebaseId: String): Todo?

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    suspend fun todoById(id: Int): Todo?

    @Query("SELECT * FROM todos WHERE firebaseId = ''")
    suspend fun pendingTodos(): List<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Int)

    @Query("DELETE FROM todos WHERE firebaseId = :firebaseId")
    suspend fun deleteTodoByFirebaseId(firebaseId: String)

    @Query("DELETE FROM todos")
    suspend fun clearAll()
}
