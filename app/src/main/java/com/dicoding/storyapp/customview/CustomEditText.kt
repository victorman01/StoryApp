package com.dicoding.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.dicoding.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class CustomEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isEmail = false
    private var isPassword = false

    private fun checkEmailValidity(email: String) {
        isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (email.isNotEmpty() && !isEmail) {
            error = "Invalid email format"
        }
    }

    private fun checkPasswordLength(password: String) {
        isPassword = password.length >= 8
        if (password.isNotEmpty() && !isPassword) {
            error = context.getString(R.string.error_password_text)
        }
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                when {
                    inputType == 33 -> checkEmailValidity(text)
                    inputType == 129 -> checkPasswordLength(text)
                }
            }
        })
    }
}
