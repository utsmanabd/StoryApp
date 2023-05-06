package com.everybodv.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.everybodv.storyapp.data.local.RemoteKeys
import com.everybodv.storyapp.data.local.StoriesDatabase
import com.everybodv.storyapp.data.remote.response.ListStoryItem
import com.everybodv.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator @Inject constructor(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPageIndex
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeysForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKeys
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }
        return try {
            val token: String = authPreferences.getToken().first()
            val response = apiService.getStory("Bearer $token", state.config.pageSize, page)
            val endOfPaginationReach = response.listStory.isEmpty()

            storiesDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storiesDatabase.remoteKeysDao().delRemote()
                    storiesDatabase.storiesDao().delStory()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReach) null else page + 1
                val keys = response.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storiesDatabase.remoteKeysDao().addAll(keys)
                response.listStory.forEach { story ->
                    val item = ListStoryItem(
                        story.photoUrl,
                        story.createdAt,
                        story.name,
                        story.description,
                        story.id,
                        story.lat,
                        story.lon
                    )
                    storiesDatabase.storiesDao().addStory(item)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReach)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeysForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storiesDatabase.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storiesDatabase.remoteKeysDao().getRemoteId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storiesDatabase.remoteKeysDao().getRemoteId(id)
            }
        }
    }

    companion object {
        const val initialPageIndex = 1
    }
}