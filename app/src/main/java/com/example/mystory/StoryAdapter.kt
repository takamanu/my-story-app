package com.example.mystory

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystory.databinding.ItemProductBinding
import com.example.mystory.datamodel.ListStoryItem
import java.util.*

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(diffCallback) {

    private val listStory = ArrayList<ListStoryItem>()

    fun setListStory(itemStory: List<ListStoryItem>) {
        val diffCallback = DiffCallback(this.listStory, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listStory.clear()
        this.listStory.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setIsRecyclable(false)

    }

//    override fun getItemCount() = listStory.size

    inner class ViewHolder(private var binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(ivStory)
                    .load(story.photoUrl)
//                    .placeholder(R.drawable.ic_baseline_image_24)
//                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(ivStory)
                tvName.text = story.name
                tvDescription.text = story.description
                tvDate.text =
                    binding.root.resources.getString(
                        R.string.date,
                        story.createdAt?.let { Utils.formatDate(it, TimeZone.getDefault().id) }
                    )

                cardView.setOnClickListener {
                    val intent = Intent(it.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
