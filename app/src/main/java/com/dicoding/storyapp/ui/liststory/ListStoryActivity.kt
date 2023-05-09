package com.dicoding.storyapp.ui.liststory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.tools.ViewModelFactory
import com.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.storyapp.ui.authentication.login.LoginActivity
import com.dicoding.storyapp.ui.maps.MapsActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var listStoryViewModel: ListStoryViewModel
    private lateinit var listStoryBinding: ActivityListStoryBinding
    private lateinit var viewModelFac: ViewModelFactory
    private lateinit var storyListAdapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listStoryBinding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(listStoryBinding.root)

        viewModelFac = ViewModelFactory.getInstance(this)
        listStoryViewModel = ViewModelProvider(this, viewModelFac)[ListStoryViewModel::class.java]

        storyListAdapter = ListStoryAdapter()
        val userToken = listStoryViewModel.getToken().value?.token.toString()

        listStoryBinding.rvStoryList.apply {
            layoutManager = LinearLayoutManager(this@ListStoryActivity)
            adapter = storyListAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyListAdapter.retry()
                }
            )
        }
        listStoryViewModel.getStoriesList(userToken).observe(this@ListStoryActivity) {
            storyListAdapter.submitData(lifecycle, it)
        }

        listStoryBinding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
        listStoryViewModel.messages.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(this@ListStoryActivity, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menuliststory, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                listStoryViewModel.logout()
                Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }

            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}