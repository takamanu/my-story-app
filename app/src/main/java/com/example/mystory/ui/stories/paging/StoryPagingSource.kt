package com.example.mystory.ui.stories.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystory.api.ApiService
import com.example.mystory.data.ListStoryItem
import com.example.mystory.utils.helper.Constant
import com.example.mystory.utils.PreferencesHelper

class StoryPagingSource(private val apiService: ApiService, private val mCtx: Context): PagingSource<Int, ListStoryItem>() {

    lateinit var sharedPref: PreferencesHelper

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        sharedPref = PreferencesHelper(mCtx)
        val token = sharedPref.getString(Constant.PREF_TOKEN)
        val bearer = "Bearer $token"
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(bearer, position, params.loadSize)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}
