package com.everybodv.storyapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.everybodv.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(listStoryItem: ListStoryItem)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getStories() : PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM stories")
    suspend fun delStory()

}