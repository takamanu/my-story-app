package com.example.mystory.vm

import androidx.lifecycle.ViewModel
import com.example.mystory.data.ListStoryItem

class DetailViewModel: ViewModel() {

    lateinit var storyItem: ListStoryItem

    fun setDetailStory(story: ListStoryItem): ListStoryItem {
        storyItem = story
        return storyItem
    }


}
