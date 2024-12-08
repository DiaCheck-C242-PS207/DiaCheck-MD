package com.project.diacheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.databinding.ItemCardBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FormAdapter(
    private val onItemClick: (ListFormItem) -> Unit
) : ListAdapter<ListFormItem, FormAdapter.FormViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormAdapter.FormViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FormViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val form = getItem(position)
        holder.bind(form)
    }


    class FormViewHolder(
        private val binding: ItemCardBinding,
        private val onItemClick: (ListFormItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(form: ListFormItem) {
            binding.ivItemName.text = formatDate(form.updated_at.toString())
            binding.ivItemDescription.text = form.history
            itemView.setOnClickListener {
                onItemClick(form)
            }
        }

        fun formatDate(inputDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(inputDate)
                outputFormat.format(date ?: "")
            } catch (e: Exception) {
                inputDate
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListFormItem> =
            object : DiffUtil.ItemCallback<ListFormItem>() {
                override fun areItemsTheSame(
                    oldItem: ListFormItem,
                    newItem: ListFormItem
                ): Boolean {
                    return oldItem.id_history == newItem.id_history
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ListFormItem,
                    newItem: ListFormItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}