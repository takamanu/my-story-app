package com.example.mystory.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListStoryItem(
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val id: String? = null,
    val lat: Double? = null
) : Parcelable
