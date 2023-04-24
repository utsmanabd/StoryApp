package com.everybodv.storyapp.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.databinding.ActivityLoginBinding
import com.everybodv.storyapp.util.*
import com.everybodv.storyapp.view.model.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        viewModel = ViewModelProvider(this, PreferencesFactory(authPreferences))[AuthViewModel::class.java]

        playAnimation()
        binding.btnLogin.setSafeOnClickListener { login() }
        binding.tvRegister.setSafeOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.customEmail.text.toString().trim()
        val password = binding.customPassword.text.toString().trim()
        viewModel.isEnabled.observe(this){ isEnabled ->
            binding.btnLogin.isEnabled = isEnabled
        }
        viewModel.isLoading.observe(this){ isLoading ->
            showLoading(binding.progressBar, isLoading)
        }

        when {
            password.isBlank() or email.isBlank() -> {
                showToast(this, getString(R.string.pls_fill_auth))
            }
            !email.matches(Const.emailPattern) -> {
                showToast(this, getString(R.string.email_format_wrong))
            }
            else -> {
                val errorMsg = getString(R.string.not_match_auth)
                viewModel.login(email, password, this, errorMsg)
                viewModel.loginUser.observe(this){ login ->
                    viewModel.setToken(login.token)
                    startActivity(Intent(this, MainActivity::class.java))
                    showToast(this, "${getString(R.string.success_login)} ${login.name}")
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tiEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tiPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.linearLayout, View.ALPHA, 1f).setDuration(500)

        val togetherOne = AnimatorSet().apply {
            playTogether(email, password)
        }
        val togetherTwo = AnimatorSet().apply {
            playTogether(btnLogin, btnRegister)
        }
        AnimatorSet().apply {
            playSequentially(title, togetherOne, togetherTwo)
            start()
        }
    }
}