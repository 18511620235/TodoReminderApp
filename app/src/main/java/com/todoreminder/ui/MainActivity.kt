package com.todoreminder.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.todoreminder.R
import com.todoreminder.data.TodoEntity
import com.todoreminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TodoAdapter

    private val addEditLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        setupRecyclerView()
        setupFab()
        observeTodos()
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onItemClick = { todo ->
                val intent = Intent(this, AddEditTodoActivity::class.java)
                intent.putExtra("todo_id", todo.id)
                addEditLauncher.launch(intent)
            },
            onCompleteClick = { todo ->
                viewModel.markAsCompleted(todo.id)
                Snackbar.make(binding.root, "任务已完成", Snackbar.LENGTH_SHORT).show()
            },
            onDeleteClick = { todo ->
                showDeleteDialog(todo)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todo = adapter.currentList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.markAsCompleted(todo.id)
                    Snackbar.make(binding.root, "任务已完成", Snackbar.LENGTH_SHORT).show()
                } else {
                    showDeleteDialog(todo)
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditTodoActivity::class.java)
            addEditLauncher.launch(intent)
        }
    }

    private fun observeTodos() {
        viewModel.activeTodos.observe(this) { todos ->
            adapter.submitList(todos)
            binding.textEmpty.visibility = if (todos.isEmpty()) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    private fun showDeleteDialog(todo: TodoEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle("删除任务")
            .setMessage("确定要删除「${todo.title}」吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteTodo(todo)
                Snackbar.make(binding.root, "任务已删除", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_completed -> {
                startActivity(Intent(this, CompletedTasksActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
