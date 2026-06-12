package com.todoreminder.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.todoreminder.R
import com.todoreminder.data.TodoEntity
import com.todoreminder.databinding.ActivityCompletedTasksBinding

class CompletedTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedTasksBinding
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: CompletedTodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletedTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "已完成任务"

        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        setupRecyclerView()
        observeCompletedTodos()
    }

    private fun setupRecyclerView() {
        adapter = CompletedTodoAdapter(
            onRestoreClick = { todo ->
                viewModel.markAsActive(todo.id)
            },
            onDeleteClick = { todo ->
                showDeleteDialog(todo)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeCompletedTodos() {
        viewModel.completedTodos.observe(this) { todos ->
            adapter.submitList(todos)
            binding.textEmpty.visibility = if (todos.isEmpty()) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    private fun showDeleteDialog(todo: TodoEntity) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("删除任务")
            .setMessage("确定要永久删除「${todo.title}」吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteTodo(todo)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
