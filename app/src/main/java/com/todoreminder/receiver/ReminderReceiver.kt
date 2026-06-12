package com.todoreminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.todoreminder.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra("todo_id", -1)
        val todoTitle = intent.getStringExtra("todo_title") ?: "待办提醒"
        val todoDescription = intent.getStringExtra("todo_description") ?: ""

        if (todoId == -1L) return

        showNotification(context, todoId, todoTitle, todoDescription)
    }

    private fun showNotification(
        context: Context,
        todoId: Long,
        title: String,
        description: String
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Create notification channel for Android O and above
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, "todo_reminder_channel")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(todoId.toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = android.app.NotificationChannel(
            "todo_reminder_channel",
            "待办提醒",
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "待办任务提醒通知"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
