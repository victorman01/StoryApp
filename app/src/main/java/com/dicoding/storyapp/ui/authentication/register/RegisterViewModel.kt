package com.dicoding.storyapp.ui.authentication.register

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.tools.Repository

class RegisterViewModel(private val rep: Repository) : ViewModel() {
    val messages = rep.messages
    val isLoading = rep.isLoading
    fun register(name: String, email: String, password: String) =
        rep.register(name, email, password)
}