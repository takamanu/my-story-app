package com.example.mystory.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryResponse(
    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("hasNextPage") // Add this line
    val hasNextPage: Boolean, // Add this line

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String?
) : Parcelable
