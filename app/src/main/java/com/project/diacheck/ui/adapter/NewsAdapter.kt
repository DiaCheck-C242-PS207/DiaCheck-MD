package com.project.diacheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.diacheck.data.remote.response.ListNewsItem
import com.project.diacheck.databinding.ItemNewsBinding

class NewsAdapter(
    private val onItemClick: (ListNewsItem) -> Unit
) : ListAdapter<ListNewsItem, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

    }

    class NewsViewHolder(
        private val binding: ItemNewsBinding,
        private val onItemClick: (ListNewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: ListNewsItem) {
            binding.tvItemName.text = news.title
            binding.ivItemDescription.text = news.content

            itemView.setOnClickListener {
                onItemClick(news)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListNewsItem> =
            object : DiffUtil.ItemCallback<ListNewsItem>() {
                override fun areItemsTheSame(oldItem: ListNewsItem, newItem: ListNewsItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ListNewsItem,
                    newItem: ListNewsItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}