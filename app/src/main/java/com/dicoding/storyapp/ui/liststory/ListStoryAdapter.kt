package com.dicoding.storyapp.ui.liststory

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ItemStoryListBinding
import com.dicoding.storyapp.response.ListStoryItem
import com.dicoding.storyapp.ui.detailstory.DetailStoryActivity

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.StoryViewHolder>(DIFF_CALLBACK_ITEM) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bindStory(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindStory(item: ListStoryItem) {
            binding.apply {
                Glide.with(itemView.context).load(item.photoUrl).into(ivItemPhoto)
                tvItemName.text = item.name
                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.NAME, item.name)
                    intent.putExtra(DetailStoryActivity.PHOTO_URL, item.photoUrl)
                    intent.putExtra(DetailStoryActivity.DESC, item.description)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemPhoto, "photo"),
                            Pair(tvItemName, "name")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK_ITEM = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

