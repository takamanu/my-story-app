package com.example.mystory.ui.stories.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystory.api.ApiService
import com.example.mystory.data.ListStoryItem
import com.example.mystory.ui.stories.paging.StoryPagingSource

class StoryRepository(private val apiService: ApiService, private val mCtx: Context) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                prefetchDistance = 1,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, mCtx)
            }
        ).liveData
    }
}
