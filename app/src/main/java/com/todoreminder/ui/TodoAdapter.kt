package com.todoreminder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.todoreminder.R
import com.todoreminder.data.TodoEntity
import com.todoreminder.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val onItemClick: (TodoEntity) -> Unit,
    private val onCompleteClick: (TodoEntity) -> Unit,
    private val onDeleteClick: (TodoEntity) -> Unit
) : ListAdapter<TodoEntity, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoEntity) {
            binding.textTitle.text = todo.title
            binding.textDescription.text = todo.description
            binding.textDescription.visibility = if (todo.description.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Show reminder info
            val reminderText = when (todo.reminderType) {
                0 -> "单次提醒"
                1 -> "重复（时间点）"
                2 -> "重复（轮询）"
                else -> "无提醒"
            }
            binding.textReminderType.text = reminderText

            binding.root.setOnClickListener {
                onItemClick(todo)
            }

            binding.buttonComplete.setOnClickListener {
                onCompleteClick(todo)
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(todo)
            }
        }
    }

    private class TodoDiffCallback : DiffUtil.ItemCallback<TodoEntity>() {
        override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
