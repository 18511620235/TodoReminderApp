package com.todoreminder

import android.app.Application
import com.todoreminder.data.TodoDatabase
import com.todoreminder.data.TodoRepository

class TodoReminderApplication : Application() {

    val database by lazy { TodoDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
