package com.dicoding.storyapp.ui.authentication.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.tools.ViewModelFactory
import com.dicoding.storyapp.ui.authentication.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var viewModelFac: ViewModelFactory
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        supportActionBar?.hide()
        viewModelFac = ViewModelFactory.getInstance(this)
        registerViewModel = ViewModelProvider(this, viewModelFac)[RegisterViewModel::class.java]


        registerBinding.btnRegister.setOnClickListener {
            val name: String = registerBinding.edRegisterName.text.toString()
            val email: String = registerBinding.edRegisterEmail.text.toString()
            val password: String = registerBinding.edRegisterPassword.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please, Input your name", Toast.LENGTH_SHORT)
                    .show()
                registerBinding.edRegisterName.text?.clear()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(
                    this@RegisterActivity, "Please, Input your email", Toast.LENGTH_SHORT
                ).show()
                registerBinding.edRegisterEmail.text?.clear()
                return@setOnClickListener
            }
            if (password.length < 8) {
                Toast.makeText(
                    this@RegisterActivity, "Password must be at least 8", Toast.LENGTH_SHORT
                ).show()
                registerBinding.edRegisterPassword.text?.clear()
                return@setOnClickListener
            }
            registerViewModel.register(name, email, password).observe(this) {
                if (it.error == false) {
                    finish()
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }

            registerViewModel.isLoading.observe(this@RegisterActivity) {
                showLoading(it)
            }
            registerViewModel.messages.observe(this) {
                it.getContentIfNotHandled()?.let { text ->
                    Toast.makeText(this@RegisterActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        registerBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}