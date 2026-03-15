package com.furkanyildirim.learningcompose.domain.usecase

import javax.inject.Inject

class TodoUseCases @Inject constructor(
    val getTodos: GetTodosUseCase,
    val addTodo: AddTodoUseCase,
    val addTodoWithId: AddTodoWithIdUseCase,
    val deleteTodo: DeleteTodoUseCase,
    val toggleTodoCompletion: ToggleTodoCompletionUseCase,
    val updateTodo: UpdateTodoUseCase,
    val syncTodos: SyncTodosUseCase
)
