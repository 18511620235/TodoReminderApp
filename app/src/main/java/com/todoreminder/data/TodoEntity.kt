package com.todoreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Date? = null,

    // Reminder type: 0=single, 1=repeat_time_points, 2=repeat_polling
    val reminderType: Int = 0,

    // For single reminder
    val singleReminderTime: Date? = null,

    // For repeat + time points mode
    val repeatStartDate: Date? = null,
    val repeatEndDate: Date? = null,
    val timePoints: String = "", // JSON array of time points like ["08:35", "09:35"]

    // For repeat + polling mode
    val pollingStartDate: Date? = null,
    val pollingEndDate: Date? = null,
    val pollingStartTime: String? = null, // "HH:mm"
    val pollingEndTime: String? = null, // "HH:mm"
    val pollingIntervalHours: Int = 0,
    val pollingIntervalMinutes: Int = 0,

    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
