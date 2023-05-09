package com.dicoding.storyapp.ui.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var detailStoryBinding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailStoryBinding.root)
        supportActionBar?.hide()
        with(detailStoryBinding) {
            tvDetailName.text = intent.getStringExtra(NAME)
            tvDetailDescription.text = intent.getStringExtra(DESC)
            Glide.with(this@DetailStoryActivity).load(intent.getStringExtra(PHOTO_URL))
                .into(ivDetailPhoto)
        }
    }

    companion object {
        const val NAME: String = "Name"
        const val DESC: String = "Desc"
        const val PHOTO_URL: String = "Photo_url"
    }
}