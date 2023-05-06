package com.everybodv.storyapp.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.local.StoriesDatabase
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.databinding.ActivityMainBinding
import com.everybodv.storyapp.util.*
import com.everybodv.storyapp.view.adapter.LoadingAdapter
import com.everybodv.storyapp.view.adapter.MainAdapter
import com.everybodv.storyapp.view.model.*
import com.google.android.gms.maps.model.LatLng
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authPreferences: AuthPreferences

    private lateinit var pagingModel: PagingModel
    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel
    private lateinit var token: Token
    private lateinit var storiesDatabase: StoriesDatabase
    private lateinit var mainAdapter: MainAdapter

    private val mainViewModel: MainViewModel by viewModels {
        PreferencesFactory(authPreferences, authRepository, this)
    }

    private var listLocation: ArrayList<LatLng>? = null
    private var listUserName: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        storiesDatabase = StoriesDatabase.getInstance(this)
        mainAdapter = MainAdapter()
        authPreferences = AuthPreferences(this)
        authRepository = AuthRepository()
        token = Token(authPreferences)
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepository, this)
        )[AuthViewModel::class.java]
        pagingModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepository, this)
        )[PagingModel::class.java]

        token.getToken().observe(this) { token ->
            if (token != null) {
                setAdapter()
                mainViewModel.getStories().observe(this) { story ->
                    mainAdapter.submitData(lifecycle, story)
                    mainAdapter.snapshot().items
                }
                mainViewModel.loading.value = false
            } else {
                Log.e(Const.TOKEN, "invalid token")
                finishAffinity()
            }
        }

        mainViewModel.loading.observe(this) { isLoading ->
            showLoading(binding.progressBar, isLoading)
        }
        binding.refresh.setOnRefreshListener { refresh() }
        binding.fabUpload.setSafeOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun setAdapter() {
        val layout = LinearLayoutManager(this@MainActivity)
        val itemDecoration = DividerItemDecoration(this@MainActivity, layout.orientation)

        mainAdapter = MainAdapter()
        mainAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStory.smoothScrollToPosition(0)
                }
            }
        })
        binding.rvStory.apply {
            layoutManager = layout
            smoothScrollToPosition(0)
            addItemDecoration(itemDecoration)
            adapter =
                mainAdapter.withLoadStateFooter(footer = LoadingAdapter { mainAdapter.retry() })
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
                builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    this.getSharedPreferences("data", 0)
                        .edit().clear().apply()
                    val toLoginIntent = Intent(this, LoginActivity::class.java)
                    toLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            token.delToken()
                            startActivity(it)
                        }
                    finishAffinity()
                }
                val alert = builder.create()
                alert.show()
            }
            R.id.change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.location -> {
                startActivity(Intent(this, MapsActivity::class.java)
                    .also {
                        it.putExtra(Const.LIST_LOCATION, listLocation)
                        it.putExtra(Const.LIST_USER_NAME, listUserName)
                    })
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