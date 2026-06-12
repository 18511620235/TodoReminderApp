package com.todoreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.todoreminder.data.TodoDatabase
import com.todoreminder.data.TodoRepository
import com.todoreminder.worker.AlarmScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            Log.d("BootReceiver", "Device booted, rescheduling reminders")

            val database = TodoDatabase.getDatabase(context)
            val repository = TodoRepository(database.todoDao())
            val scheduler = AlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Collect all active (non-completed) todos
                    repository.activeTodos.collect { todos ->
                        for (todo in todos) {
                            if (todo.reminderType == 0) {
                                // Only reschedule if the reminder time is still in the future
                                todo.singleReminderTime?.let {
                                    if (it.time > System.currentTimeMillis()) {
                                        scheduler.scheduleReminder(todo)
                                    }
                                }
                            } else {
                                // For repeat modes, reschedule (the scheduler will filter past times)
                                scheduler.scheduleReminder(todo)
                            }
                        }
                        // Only need first emission
                        return@launch
                    }
                    Log.d("BootReceiver", "Reminders rescheduled successfully")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to reschedule reminders", e)
                }
            }
        }
    }
}
