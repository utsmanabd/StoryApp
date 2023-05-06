package com.everybodv.storyapp.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.util.showToast
import com.everybodv.storyapp.view.model.Token

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var token: Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authPreferences = AuthPreferences(this)
        token = Token(authPreferences)

        Handler(Looper.getMainLooper()).postDelayed({
            token()
        }, 1500)

    }

    private fun token() {
        token.getToken().observe(this) { key ->
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