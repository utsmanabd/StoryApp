package com.everybodv.storyapp.view.ui

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.databinding.ActivityDetailStoryBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.util.withDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_story)

        @Suppress("DEPRECATION")
        val detail = intent.getParcelableExtra<ListStoryItem>(Const.DETAIL) as ListStoryItem

        val geocoder = Geocoder(this, Locale.getDefault())
        val address = detail.lat?.let { latitude ->
            detail.lon?.let { longitude ->
                geocoder.getFromLocation(latitude, longitude, 1)
            }
        }
        val cityName = address?.get(0)?.subAdminArea
        val stateName = address?.get(0)?.adminArea
        val countryName = address?.get(0)?.countryName

        val addressName = "$cityName, $stateName, $countryName"

        binding.apply {
            tvUsername.text = detail.name
            tvDescription.text = detail.description
            tvDates.text = detail.createdAt.withDateFormat()
            Glide.with(this@DetailStoryActivity)
                .load(detail.photoUrl)
                .transform(CenterInside(), RoundedCorners(25))
                .into(ivImageStory)
            if (address != null) {
                tvLocation.text = addressName
            } else {
                tvLocation.isVisible = false
            }
        }

        val isFavorite = MutableLiveData(false)
        binding.ivFavorite.setOnClickListener {
            if (isFavorite.value == false) {
                binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_24)
                isFavorite.value = true
            } else {
                binding.ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                isFavorite.value = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}