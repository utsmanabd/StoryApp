package com.everybodv.storyapp.view.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.databinding.ItemRowMenuBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.util.setSafeOnClickListener
import com.everybodv.storyapp.view.ui.DetailStoryActivity
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter : PagingDataAdapter<ListStoryItem, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemRowMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(item: ListStoryItem) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("ID")
            val time = dateFormat.parse(item.createdAt)?.time
            val prettyTime = PrettyTime(Locale.getDefault())
            val date = prettyTime.format(time?.let { Date(it) })

            with(binding) {
                tvUsername.text = item.name
                tvDateAdded.text = date
                tvDescription.text = item.description
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(ivImageStory)
            }
            itemView.setSafeOnClickListener {
                with(it.context) {
                    val toDetailIntent = Intent(this, DetailStoryActivity::class.java)
                    toDetailIntent.putExtra(Const.DETAIL, item)
                    startActivity(toDetailIntent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        val binding = ItemRowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
            }

            override fun onRemoved(position: Int, count: Int) {
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
            }
        }
    }
}