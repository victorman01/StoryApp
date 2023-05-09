package com.dicoding.storyapp.ui.liststory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.storyapp.tools.Repository

class ListStoryViewModel(private val rep: Repository) : ViewModel() {
    val messages = rep.messages
    fun logout() = rep.logout()
    fun getStoriesList(token: String) = rep.getStories(token).cachedIn(viewModelScope)
    fun getStoriesLocList(token: String) = rep.getStoriesLoc(token)
    fun getToken() = rep.getUser()
}