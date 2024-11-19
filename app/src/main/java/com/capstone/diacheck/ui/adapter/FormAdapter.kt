package com.capstone.diacheck.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.diacheck.R

class FormAdapter : ListAdapter<ListFormItem, FormAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide
                .with(binding.root.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.ivItemPhoto)
            binding.ivItemName.text = story.name
            binding.ivItemDescription.text = story.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(STORY, story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "image"),
                        Pair(binding.ivItemName, "title"),
                        Pair(binding.ivItemDescription, "desc"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListFormItem>() {
            override fun areItemsTheSame(oldItem: ListFormItem, newItem: ListFormItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListFormItem,
                newItem: ListFormItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}