package com.dicoding.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.tools.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val rep: Repository) : ViewModel() {
    val messages = rep.messages
    val checkAdd = rep.checkAdd
    val isLoading = rep.isLoading
    fun addNewStory(
        token: String,
        photo: MultipartBody.Part,
        desc: RequestBody,
        lat: Double? = null,
        long: Double? = null
    ) =
        rep.addNewStory(token, photo, desc, lat, long)

    fun getToken() = rep.getUser()
}