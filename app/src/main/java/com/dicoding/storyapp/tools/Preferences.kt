package com.dicoding.storyapp.tools

import android.content.Context
import com.dicoding.storyapp.response.LoginResult

class Preferences(context: Context) {
    private val preferences =
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setLogin(name: String, token: String) {
        val editor = preferences.edit()
        editor.putString(NAME, name)
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun setLogout() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun getLogin(): LoginResult {
        return LoginResult(
            preferences.getString(NAME, ""),
            preferences.getString(TOKEN, "")
        )
    }

    companion object {
        private val NAME = "name"
        private val TOKEN = "token"
        private val PREFERENCES = "pref"

        private var instance: Preferences? = null

        fun getInstance(context: Context): Preferences =
            instance ?: synchronized(this) {
                instance ?: Preferences(context)
            }
    }
}