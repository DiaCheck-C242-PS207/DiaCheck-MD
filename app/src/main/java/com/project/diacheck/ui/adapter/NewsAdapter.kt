package com.project.diacheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.diacheck.R
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.databinding.ItemNewsBinding

class NewsAdapter(
    private val onItemClick: (NewsEntity) -> Unit
) : ListAdapter<NewsEntity, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

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
        private val onItemClick: (NewsEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsEntity) {
            binding.tvItemName.text = news.title

            Glide.with(itemView.context)
                .load(news.thumbnail)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fitCenter()
                )
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onItemClick(news)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<NewsEntity> =
            object : DiffUtil.ItemCallback<NewsEntity>() {
                override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: NewsEntity,
                    newItem: NewsEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}