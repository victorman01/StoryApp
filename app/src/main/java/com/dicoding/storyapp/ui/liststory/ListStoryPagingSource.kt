package com.dicoding.storyapp.ui.liststory

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.response.ListStoryItem

class ListStoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStoryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(token, position, params.loadSize)
            LoadResult.Page(
                data = responseData.body()?.listStory ?: emptyList(),
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            Log.e("PAGING", exception.toString())
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {anchorPosition->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}