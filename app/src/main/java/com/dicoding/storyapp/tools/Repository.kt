package com.dicoding.storyapp.tools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.response.AddNewStoryResponse
import com.dicoding.storyapp.response.ListStoryItem
import com.dicoding.storyapp.response.LoginResponse
import com.dicoding.storyapp.response.RegisterResponse
import com.dicoding.storyapp.response.StoriesResponse
import com.dicoding.storyapp.ui.liststory.ListStoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository constructor(
    private val apiService: ApiService, private val preferences: Preferences
) {
    private val _messages = MutableLiveData<Event<String>>()
    val messages: LiveData<Event<String>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _checkAdd = MutableLiveData<Boolean>()
    val checkAdd: LiveData<Boolean> = _checkAdd

    fun login(email: String, password: String): LiveData<LoginResponse> {
        _isLoading.value = true
        val loginResponse = MutableLiveData<LoginResponse>()
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>, response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    loginResponse.value = response.body()
                    _messages.value = Event("Login Success")
                } else {
                    _messages.value = Event("Login Failed. Message: " + response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _messages.value = Event(TAG + ", " + t.message.toString())
                _isLoading.value = false
            }
        })
        return loginResponse
    }

    fun register(name: String, email: String, password: String): LiveData<RegisterResponse> {
        _isLoading.value = true
        val registerResponse = MutableLiveData<RegisterResponse>()
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    registerResponse.value = response.body()
                    _messages.value = Event("Register Success")
                } else {
                    _messages.value = Event("Register Failed. Message: " + response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _messages.value = Event(TAG + ", " + t.message.toString())
                _isLoading.value = false
            }
        })
        return registerResponse
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                ListStoryPagingSource(apiService, token)
            }
        ).liveData
    }

    fun getStoriesLoc(token: String): LiveData<List<ListStoryItem>?> {
        _isLoading.value = true
        val storiesList = MutableLiveData<List<ListStoryItem>?>()
        val client = apiService.getAllStoriesLoc(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>, response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    storiesList.value = response.body()?.listStory
                } else {
                    _messages.value = Event(TAG + ", " + response.message())
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _messages.value = Event(TAG + ", " + t.message.toString())
                _isLoading.value = false
            }
        })
        return storiesList
    }

    fun addNewStory(
        token: String,
        photo: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        long: Double?
    ) {
        _isLoading.value = true
        val client = apiService.addNewStory(token, photo, desc, lat, long)
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>, response: Response<AddNewStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _checkAdd.value = true
                    _messages.value = Event("Add story success")
                } else {
                    _checkAdd.value = false
                    _messages.value = Event("Add story failed. Message: " + response.message())
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _messages.value = Event(TAG + ", " + t.message)
                _isLoading.value = false
            }
        })
    }

    fun loginUser(name: String, token: String) = preferences.setLogin(name, token)

    fun logout() = preferences.setLogout()

    fun getUser() = MutableLiveData(preferences.getLogin())

    companion object {
        private const val TAG = "Repository"
        private var instance: Repository? = null
        fun getInstance(pref: Preferences, apiService: ApiService): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(
                    apiService, pref
                )
            }.also { instance = it }
    }
}