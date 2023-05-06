package com.everybodv.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.everybodv.storyapp.databinding.ItemLoadingBinding
import com.everybodv.storyapp.util.setSafeOnClickListener

class LoadingAdapter(private var retry: () -> Unit) :
    LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {
    inner class LoadingViewHolder(private val binding: ItemLoadingBinding, retry: (() -> Unit)) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvError.text = loadState.error.localizedMessage
            }
            binding.tvError.isVisible = loadState is LoadState.Error
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.progressBarRetry.isVisible = loadState is LoadState.Loading
        }

        init {
            binding.btnRetry.setSafeOnClickListener { retry.invoke() }
        }
    }

    override fun onBindViewHolder(
        holder: LoadingViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }

}