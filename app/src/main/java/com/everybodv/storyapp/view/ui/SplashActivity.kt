package com.everybodv.storyapp.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.util.PreferencesFactory
import com.everybodv.storyapp.util.showToast
import com.everybodv.storyapp.view.model.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authPreferences = AuthPreferences(this)
        viewModel = ViewModelProvider(this, PreferencesFactory(authPreferences))[AuthViewModel::class.java]

        Handler(Looper.getMainLooper()).postDelayed({
            token()
        }, 1500)

    }

    private fun token() {
        viewModel.getToken().observe(this){ key ->
            if (key != null) {
                if (!key.equals("NotFound")) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                showToast(this, getString(R.string.error))
            }
        }
    }
}