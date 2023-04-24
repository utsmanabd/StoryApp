package com.everybodv.storyapp.view.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.databinding.ItemRowMenuBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.util.setSafeOnClickListener
import com.everybodv.storyapp.view.ui.DetailStoryActivity
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class StoriesAdapter(private val listStory : List<ListStoryItem>): RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRowMenuBinding): RecyclerView.ViewHolder(binding.root) {

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
                    .into(ivImageStory)
            }
            itemView.setSafeOnClickListener{
                with(it.context) {
                    val toDetailIntent = Intent(this, DetailStoryActivity::class.java)
                    toDetailIntent.putExtra(Const.DETAIL, item)
                    startActivity(toDetailIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesAdapter.ViewHolder {
        val binding = ItemRowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesAdapter.ViewHolder, position: Int) {
        val stories = listStory[position]
        holder.bind(stories)
        val ivFavorite = holder.binding.ivFavorite
        val isFavorite = MutableLiveData(false)
        ivFavorite.setOnClickListener {
            if (isFavorite.value == false) {
                ivFavorite.setImageResource(R.drawable.baseline_favorite_24)
                isFavorite.value = true
            } else {
                ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                isFavorite.value = false
            }
        }
    }

    override fun getItemCount(): Int = listStory.size
}