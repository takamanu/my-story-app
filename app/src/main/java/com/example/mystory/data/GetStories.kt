package com.example.mystory.data

data class GetStories(
    val error: Boolean,
    val listStory: ArrayList<ListStoryItem>,
    val message: String
)
