package com.todoreminder.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.todoreminder.data.TodoEntity
import com.todoreminder.receiver.ReminderReceiver
import java.util.*

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(todo: TodoEntity) {
        when (todo.reminderType) {
            0 -> scheduleSingleReminder(todo)
            1 -> scheduleRepeatTimePoints(todo)
            2 -> scheduleRepeatPolling(todo)
        }
    }

    fun cancelReminder(todoId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleSingleReminder(todo: TodoEntity) {
        todo.singleReminderTime?.let { reminderTime ->
            val triggerTime = reminderTime.time

            if (triggerTime > System.currentTimeMillis()) {
                setAlarm(todo.id, triggerTime, todo.title, todo.description, todo.reminderType)
            }
        }
    }

    private fun scheduleRepeatTimePoints(todo: TodoEntity) {
        // Parse time points and schedule alarms for each time point within the date range
        // This is a simplified version - in reality, you'd need to calculate all alarm times
        // and schedule them individually or use a different approach like WorkManager

        todo.repeatStartDate?.let { startDate ->
            todo.repeatEndDate?.let { endDate ->
                val timePointsList = parseTimePoints(todo.timePoints)

                val calendar = Calendar.getInstance()
                calendar.time = startDate

                while (calendar.time <= endDate) {
                    for (timePoint in timePointsList) {
                        val (hour, minute) = timePoint.split(":").map { it.toInt() }
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)

                        if (calendar.timeInMillis > System.currentTimeMillis()) {
                            setAlarm(
                                todo.id,
                                calendar.timeInMillis,
                                todo.title,
                                todo.description,
                                todo.reminderType
                            )
                        }
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
    }

    private fun scheduleRepeatPolling(todo: TodoEntity) {
        // Schedule polling reminders within the date range and time window
        todo.pollingStartDate?.let { startDate ->
            todo.pollingEndDate?.let { endDate ->
                todo.pollingStartTime?.let { startTime ->
                    todo.pollingEndTime?.let { endTime ->
                        val intervalMillis = ((todo.pollingIntervalHours * 60 + todo.pollingIntervalMinutes) * 60 * 1000).toLong()

                        if (intervalMillis > 0) {
                            var triggerTime = startDate.time

                            while (triggerTime <= endDate.time) {
                                if (triggerTime > System.currentTimeMillis()) {
                                    setAlarm(
                                        todo.id,
                                        triggerTime,
                                        todo.title,
                                        todo.description,
                                        todo.reminderType
                                    )
                                }
                                triggerTime += intervalMillis
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setAlarm(id: Long, triggerTime: Long, title: String, description: String, reminderType: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("todo_id", id)
            putExtra("todo_title", title)
            putExtra("todo_description", description)
            putExtra("reminder_type", reminderType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun parseTimePoints(timePointsJson: String): List<String> {
        // Simple parsing - in reality, you'd use Gson or similar
        return if (timePointsJson.isEmpty()) {
            emptyList()
        } else {
            timePointsJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
        }
    }
}
