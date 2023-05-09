package com.dicoding.storyapp.ui.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.ui.authentication.register.RegisterActivity
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.tools.ViewModelFactory
import com.dicoding.storyapp.ui.liststory.ListStoryActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModelFac: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        viewModelFac = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, viewModelFac)[LoginViewModel::class.java]
        supportActionBar?.hide()
        loginViewModel.getUser().observe(this) {
            if (it.token?.isEmpty() == false) {
                moveHome()
            }
        }
        loginBinding.btnLogin.setOnClickListener {
            val email: String = loginBinding.edLoginEmail.text.toString()
            val pass: String = loginBinding.edLoginPassword.text.toString()
            if (email.isNotEmpty() && pass.length >= 8) {
                loginViewModel.login(email, pass).observe(this@LoginActivity) {
                    if (!(it.error as Boolean)) {
                        loginViewModel.setLogin(
                            it.loginResult?.name.toString(),
                            "Bearer " + (it.loginResult?.token.toString())
                        )
                        moveHome()
                    } else {
                        Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            loginViewModel.messages.observe(this) {
                it.getContentIfNotHandled()?.let { text ->
                    Toast.makeText(this@LoginActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
            loginViewModel.isLoading.observe(this@LoginActivity) {
                showLoading(it)
            }
        }
        loginBinding.tvSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loginBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun moveHome() {
        val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
        startActivity(intent)
        finish()
    }
}