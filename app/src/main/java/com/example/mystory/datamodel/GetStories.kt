package com.example.mystory.datamodel

data class GetStories(
    val error: Boolean,
    val listStory: ArrayList<ListStoryItem>,
    val message: String
)