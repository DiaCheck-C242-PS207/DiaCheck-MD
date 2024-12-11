package com.project.diacheck.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.diacheck.R
import com.project.diacheck.data.remote.response.ListNewsItem
import com.project.diacheck.data.remote.response.News
import com.project.diacheck.databinding.ItemNewsBinding

class NewsAdapter(
    private val onItemClick: (ListNewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val items = mutableListOf<ListNewsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newsList: List<ListNewsItem>) {
        items.clear()
        items.addAll(newsList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitSingleItem(news: News) {
        Log.d("NewsAdapter", "submitSingleItem: ${news.title}")
        items.clear()
        items.add(
            ListNewsItem(
                id_article = news.id_article,
                id_users = news.id_users,
                thumbnail = news.thumbnail,
                title = news.title,
                body = news.body,
                created_at = news.created_at,
                updated_at = news.updated_at
            )
        )
        notifyDataSetChanged()
    }


    class NewsViewHolder(
        private val binding: ItemNewsBinding,
        private val onItemClick: (ListNewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: ListNewsItem) {
            Glide.with(binding.ivItemImage.context)
                .load(news.thumbnail)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(binding.ivItemImage)
            binding.tvItemName.text = news.title
            binding.ivItemDescription.text =
                HtmlCompat.fromHtml(news.body, HtmlCompat.FROM_HTML_MODE_LEGACY)

            itemView.setOnClickListener {
                onItemClick(news)
            }
        }
    }
}
