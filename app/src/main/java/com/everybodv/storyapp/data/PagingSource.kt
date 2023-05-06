package com.everybodv.storyapp.data

import androidx.paging.PagingData
import androidx.paging.PagingState
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

class PagingSource(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences,
) : androidx.paging.PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {

        return try {
            val position = params.key ?: initialPageIndex
            val token = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $token", params.loadSize, position)
            val listStory = response.listStory

            LoadResult.Page(
                listStory,
                prevKey = if (position == initialPageIndex) null else position - 1,
                nextKey = if (listStory.isEmpty()) null else position + 1
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    companion object {
        const val initialPageIndex = 1

        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

}