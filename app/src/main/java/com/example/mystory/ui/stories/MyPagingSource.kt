package com.example.mystory.ui.stories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystory.api.ApiService
import com.example.mystory.data.ListStoryItem
import retrofit2.HttpException

class MyPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    var logInstance: Log? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val currentPage = params.key ?: 1
            Log.d("MainViewModel", "Cek ricek: $token, $currentPage, ${params.loadSize}")
            val response = apiService.getAllPagingStories("Bearer $token", currentPage, params.loadSize)
            Log.d("MainViewModel", "Paging: $response")

            val data = response.body()?.listStory
            Log.d("MainViewModel", "Response data: $data")
            val responseData = mutableListOf<ListStoryItem>()
            if (data != null) {
                responseData.addAll(data)
            }


            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        // Return the key for refreshing the data (e.g., the initial page key)
        // You can implement the logic to determine the refresh key based on your requirements
        return null
    }
}


