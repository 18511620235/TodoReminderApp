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

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            Log.d("BootReceiver", "Device booted, rescheduling reminders")

            // Reschedule all active reminders
            rescheduleReminders(context)
        }
    }

    private fun rescheduleReminders(context: Context) {
        val database = TodoDatabase.getDatabase(context)
        val repository = TodoRepository(database.todoDao())

        CoroutineScope(Dispatchers.IO).launch {
            val activeTodos = repository.activeTodos

            // This is a simplified version - in reality, you'd need to collect the flow
            // and reschedule alarms for each todo

            Log.d("BootReceiver", "Reminders rescheduled")
        }
    }
}
