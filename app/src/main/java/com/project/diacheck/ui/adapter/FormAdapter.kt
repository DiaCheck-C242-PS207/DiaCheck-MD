package com.project.diacheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.databinding.ItemCardBinding

class FormAdapter(
    private val onItemClick: (HistoryEntity) -> Unit
) : ListAdapter<HistoryEntity, FormAdapter.FormViewHolder>(DIFF_CALLBACK) {
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
        private val onItemClick: (HistoryEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(form: HistoryEntity) {
            binding.ivItemName.text = form.updated_at
            binding.ivItemDescription.text = form.history
            itemView.setOnClickListener {
                onItemClick(form)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoryEntity> =
            object : DiffUtil.ItemCallback<HistoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: HistoryEntity,
                    newItem: HistoryEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: HistoryEntity,
                    newItem: HistoryEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}