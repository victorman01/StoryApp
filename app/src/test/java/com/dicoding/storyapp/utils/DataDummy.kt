package com.dicoding.storyapp.utils

import androidx.paging.PagingData
import com.dicoding.storyapp.response.ListStoryItem

object DataDummy {

    fun generateDummyStory(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 1..10) {
            val story = ListStoryItem(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Dimas",
                description = "Lorem Ipsum",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                lat = -10.212,
                lon = -16.002,
            )
            storyList.add(story)
        }
        return storyList
    }
}
