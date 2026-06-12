package com.todoreminder.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.todoreminder.R
import com.todoreminder.data.TodoEntity
import com.todoreminder.data.TodoRepository
import com.todoreminder.databinding.ActivityAddEditTodoBinding
import com.todoreminder.TodoReminderApplication
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AddEditTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTodoBinding
    private var todoId: Long = 0
    private var isEditMode = false
    private lateinit var repository: TodoRepository

    // Reminder type: 0=single, 1=repeat_time_points, 2=repeat_polling
    private var selectedReminderType = 0

    private val timePoints = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as TodoReminderApplication).repository

        todoId = intent.getLongExtra("todo_id", 0)
        isEditMode = todoId > 0

        setupToolbar()
        setupReminderTypeSpinner()
        setupTimePointsList()
        setupButtons()
        setupDateTimePickers()

        if (isEditMode) {
            loadTodoData()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = if (isEditMode) "编辑任务" else "新建任务"
    }

    private fun setupReminderTypeSpinner() {
        val types = arrayOf("单次提醒", "重复 + 按时间点", "重复 + 轮询")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReminderType.adapter = adapter

        binding.spinnerReminderType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedReminderType = position
                updateReminderTypeUI()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateReminderTypeUI() {
        binding.layoutSingleReminder.visibility = if (selectedReminderType == 0) View.VISIBLE else View.GONE
        binding.layoutRepeatTimePoints.visibility = if (selectedReminderType == 1) View.VISIBLE else View.GONE
        binding.layoutRepeatPolling.visibility = if (selectedReminderType == 2) View.VISIBLE else View.GONE
    }

    private fun setupTimePointsList() {
        // Setup RecyclerView or ListView for time points
        // For simplicity, using a TextView to display added time points
        updateTimePointsDisplay()
    }

    private fun updateTimePointsDisplay() {
        binding.textTimePoints.text = if (timePoints.isEmpty()) {
            "未添加时间点"
        } else {
            timePoints.joinToString(", ")
        }
    }

    private fun setupButtons() {
        binding.buttonAddTimePoint.setOnClickListener {
            showTimePickerForTimePoint()
        }

        binding.buttonSave.setOnClickListener {
            saveTodo()
        }
    }

    private fun setupDateTimePickers() {
        binding.editSingleDate.setOnClickListener {
            showDatePicker { date ->
                binding.editSingleDate.setText(dateFormat.format(date))
            }
        }

        binding.editSingleTime.setOnClickListener {
            showTimePicker { time ->
                binding.editSingleTime.setText(timeFormat.format(time))
            }
        }

        binding.editRepeatStartDate.setOnClickListener {
            showDatePicker { date ->
                binding.editRepeatStartDate.setText(dateFormat.format(date))
            }
        }

        binding.editRepeatEndDate.setOnClickListener {
            showDatePicker { date ->
                binding.editRepeatEndDate.setText(dateFormat.format(date))
            }
        }

        binding.editPollingStartDate.setOnClickListener {
            showDatePicker { date ->
                binding.editPollingStartDate.setText(dateFormat.format(date))
            }
        }

        binding.editPollingEndDate.setOnClickListener {
            showDatePicker { date ->
                binding.editPollingEndDate.setText(dateFormat.format(date))
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showTimePickerForTimePoint() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val timePoint = String.format("%02d:%02d", hourOfDay, minute)
                timePoints.add(timePoint)
                updateTimePointsDisplay()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun loadTodoData() {
        CoroutineScope(Dispatchers.IO).launch {
            val todo = repository.getTodoById(todoId)
            withContext(Dispatchers.Main) {
                todo?.let {
                    binding.editTitle.setText(it.title)
                    binding.editDescription.setText(it.description)
                    binding.spinnerReminderType.setSelection(it.reminderType)

                    // Load other fields based on reminder type
                    // ... (implementation details)
                }
            }
        }
    }

    private fun saveTodo() {
        val title = binding.editTitle.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入任务标题", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.editDescription.text.toString()

        val todo = TodoEntity(
            id = if (isEditMode) todoId else 0,
            title = title,
            description = description,
            reminderType = selectedReminderType,
            isCompleted = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (isEditMode) {
                repository.updateTodo(todo)
            } else {
                repository.insertTodo(todo)
            }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
