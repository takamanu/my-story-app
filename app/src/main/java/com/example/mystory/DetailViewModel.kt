package com.example.mystory

import android.content.ContentValues.TAG
import android.util.Config
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystory.api.ApiConfig
import com.example.mystory.datamodel.ListStoryItem
import com.example.mystory.datamodel.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel: ViewModel() {

    lateinit var storyItem: ListStoryItem

    fun setDetailStory(story: ListStoryItem): ListStoryItem {
        storyItem = story
        return storyItem
    }


}
