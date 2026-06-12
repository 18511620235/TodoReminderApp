package com.todoreminder.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class TodoRepository(private val todoDao: TodoDao) {

    val activeTodos: Flow<List<TodoEntity>> = todoDao.getActiveTodos()
    val completedTodos: Flow<List<TodoEntity>> = todoDao.getCompletedTodos()

    suspend fun insertTodo(todo: TodoEntity): Long {
        return todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }

    suspend fun getTodoById(id: Long): TodoEntity? {
        return todoDao.getTodoById(id)
    }

    suspend fun markAsCompleted(id: Long) {
        todoDao.markAsCompleted(id, Date())
    }

    suspend fun markAsActive(id: Long) {
        todoDao.markAsActive(id)
    }
}
