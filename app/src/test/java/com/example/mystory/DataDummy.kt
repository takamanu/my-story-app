package com.example.mystory

import com.example.mystory.data.GetStories
import com.example.mystory.data.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {

        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "author $i",
                "description $i",
                0.0,
                "$i",
                0.0,
            )
            items.add(story)
        }

        return items

    }

    fun generateDummyNewsResponse(): GetStories {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val news = ListStoryItem(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "author $i",
                "description $i",
                0.0,
                "$i",
                0.0,
            )
            storyList.add(news)
        }
        return GetStories(false, storyList, "Success")
    }

}
