package com.dicoding.storyapp.tools

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.ui.addstory.AddStoryViewModel
import com.dicoding.storyapp.ui.authentication.login.LoginViewModel
import com.dicoding.storyapp.ui.authentication.register.RegisterViewModel
import com.dicoding.storyapp.ui.liststory.ListStoryViewModel

class ViewModelFactory(private val rep: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(rep) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(rep) as T
            }

            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(rep) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(rep) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideRepository(context))
        }
    }
}