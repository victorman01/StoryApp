package com.dicoding.storyapp.ui.authentication.login

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.tools.Repository

class LoginViewModel(private val rep: Repository) : ViewModel() {
    val messages = rep.messages
    val isLoading = rep.isLoading
    fun login(email: String, pass: String) = rep.login(email, pass)
    fun setLogin(name: String, token: String) = rep.loginUser(name, token)
    fun getUser() = rep.getUser()
}