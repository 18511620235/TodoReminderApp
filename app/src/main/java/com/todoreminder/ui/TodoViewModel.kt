package com.todoreminder.ui

import android.app.Application
import androidx.lifecycle.*
import com.todoreminder.data.TodoEntity
import com.todoreminder.data.TodoRepository
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository

    val activeTodos: LiveData<List<TodoEntity>>
    val completedTodos: LiveData<List<TodoEntity>>

    init {
        val todoDao = (application as TodoReminderApplication).database.todoDao()
        repository = TodoRepository(todoDao)
        activeTodos = repository.activeTodos.asLiveData()
        completedTodos = repository.completedTodos.asLiveData()
    }

    fun insertTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.insertTodo(todo)
    }

    fun updateTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: TodoEntity) = viewModelScope.launch {
        repository.deleteTodo(todo)
    }

    fun markAsCompleted(id: Long) = viewModelScope.launch {
        repository.markAsCompleted(id)
    }

    fun markAsActive(id: Long) = viewModelScope.launch {
        repository.markAsActive(id)
    }

    suspend fun getTodoById(id: Long): TodoEntity? {
        return repository.getTodoById(id)
    }
}
