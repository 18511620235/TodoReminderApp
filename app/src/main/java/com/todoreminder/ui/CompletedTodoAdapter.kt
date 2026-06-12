package com.todoreminder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.todoreminder.R
import com.todoreminder.data.TodoEntity
import com.todoreminder.databinding.ItemCompletedTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class CompletedTodoAdapter(
    private val onRestoreClick: (TodoEntity) -> Unit,
    private val onDeleteClick: (TodoEntity) -> Unit
) : ListAdapter<TodoEntity, CompletedTodoAdapter.CompletedTodoViewHolder>(CompletedTodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTodoViewHolder {
        val binding = ItemCompletedTodoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CompletedTodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedTodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompletedTodoViewHolder(private val binding: ItemCompletedTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoEntity) {
            binding.textTitle.text = todo.title
            binding.textDescription.text = todo.description
            binding.textDescription.visibility = if (todo.description.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Show completion date
            todo.completedAt?.let { completedAt ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                binding.textCompletedAt.text = "完成于: ${dateFormat.format(completedAt)}"
                binding.textCompletedAt.visibility = View.VISIBLE
            } ?: run {
                binding.textCompletedAt.visibility = View.GONE
            }

            binding.buttonRestore.setOnClickListener {
                onRestoreClick(todo)
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(todo)
            }
        }
    }

    private class CompletedTodoDiffCallback : DiffUtil.ItemCallback<TodoEntity>() {
        override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
