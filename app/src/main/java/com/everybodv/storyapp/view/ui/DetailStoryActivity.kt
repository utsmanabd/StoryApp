package com.everybodv.storyapp.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.databinding.ActivityDetailStoryBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.util.withDateFormat
import com.everybodv.storyapp.view.model.StoriesViewModel

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: StoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel = ViewModelProvider(this)[StoriesViewModel::class.java]

        @Suppress("DEPRECATION")
        val detail = intent.getParcelableExtra<ListStoryItem>(Const.DETAIL) as ListStoryItem

        binding.apply {
            tvUsername.text = detail.name
            tvDescription.text = detail.description
            tvDateAdded.text = detail.createdAt.withDateFormat()
            Glide.with(this@DetailStoryActivity)
                .load(detail.photoUrl)
                .transform(CenterInside(), RoundedCorners(25))
                .into(ivImageStory)
        }

        viewModel.isFavorite.observe(this){ isFavorite ->
            binding.ivFavorite.setOnClickListener {
                if (isFavorite == false) {
                    binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_24)
                    viewModel.isFavorite.value = true
                } else {
                    binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                    viewModel.isFavorite.value = false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}