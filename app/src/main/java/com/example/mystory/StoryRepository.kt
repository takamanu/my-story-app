package com.example.mystory

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mystory.datamodel.ListStoryItem
import kotlinx.coroutines.flow.Flow

//class StoryRepository {
//    private val pagingConfig = PagingConfig(
//        pageSize = 20, // Adjust the page size as per your requirements
//        enablePlaceholders = false
//    )
//
//    fun getStoryList(token: String): Flow<PagingData<ListStoryItem>> {
//        return Pager(
//            config = pagingConfig,
//            pagingSourceFactory = { StoryPagingSource(token) }
//        ).flow
//    }
//}
