package com.everybodv.storyapp.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.databinding.ActivityMainBinding
import com.everybodv.storyapp.util.Const
import com.everybodv.storyapp.view.adapter.StoriesAdapter
import com.everybodv.storyapp.view.model.AuthViewModel
import com.everybodv.storyapp.view.model.StoriesViewModel
import com.everybodv.storyapp.util.PreferencesFactory
import com.everybodv.storyapp.util.setSafeOnClickListener
import com.everybodv.storyapp.util.showLoading
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storiesViewModel: StoriesViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authPreferences: AuthPreferences
    private lateinit var storiesAdapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        authPreferences = AuthPreferences(this)
        authViewModel = ViewModelProvider(this, PreferencesFactory(authPreferences))[AuthViewModel::class.java]
        storiesViewModel = ViewModelProvider(this)[StoriesViewModel::class.java]

        authViewModel.getToken().observe(this){ token ->
            if (token != null) {
                storiesViewModel.getStories("Bearer $token", this)
            } else {
                Log.e(Const.TOKEN, "invalid token")
            }
        }

        storiesViewModel.isLoading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }

        val layout = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, layout.orientation)

        storiesViewModel.stories.observe(this) { story ->
            storiesAdapter = StoriesAdapter(story)
            binding.rvStory.apply {
                setHasFixedSize(true)
                addItemDecoration(itemDecoration)
                layoutManager = layout
                adapter = storiesAdapter
            }
        }

        binding.refresh.setOnRefreshListener { refresh() }
        binding.fabUpload.setSafeOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

    }

    private fun refresh() {
        binding.refresh.isRefreshing = true
        Timer().schedule(1000) {
            binding.refresh.isRefreshing = false
        }
        binding.rvStory.smoothScrollBy(0, 0)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.logout_ask))
                builder.setNegativeButton(getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton(getString(R.string.yes)){_, _ ->
                    this.getSharedPreferences("data", 0)
                        .edit().clear().apply()
                    val toLoginIntent = Intent(this, LoginActivity::class.java)
                    toLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            authViewModel.delToken()
                            startActivity(it)
                        }
                    finish()
                }
                val alert = builder.create()
                alert.show()
            }
            R.id.change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        finishAffinity()
    }
}