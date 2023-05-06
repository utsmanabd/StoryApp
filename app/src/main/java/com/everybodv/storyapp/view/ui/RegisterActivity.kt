package com.everybodv.storyapp.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.everybodv.storyapp.R
import com.everybodv.storyapp.data.AuthPreferences
import com.everybodv.storyapp.data.repository.AuthRepository
import com.everybodv.storyapp.databinding.ActivityRegisterBinding
import com.everybodv.storyapp.util.*
import com.everybodv.storyapp.view.model.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        authRepository = AuthRepository()
        authViewModel = ViewModelProvider(
            this,
            PreferencesFactory(authPreferences, authRepository, this)
        )[AuthViewModel::class.java]

        binding.btnRegister.setSafeOnClickListener { register() }
        binding.tvToLogin.setOnClickListener { finish() }
    }

    private fun register() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.customEmail.text.toString().trim()
        val password = binding.customPassword.text.toString().trim()

        authViewModel.isEnabled.observe(this) { isEnabled ->
            binding.btnRegister.isEnabled = isEnabled
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            showLoading(binding.progressBarReg, isLoading)
        }

        when {
            password.isBlank() or email.isBlank() or username.isBlank() -> {
                showToast(this, getString(R.string.pls_fill_auth))
            }
            password.length < 8 -> {
                showToast(this, getString(R.string.pass_not_valid))
            }
            !email.matches(Const.emailPattern) -> {
                showToast(this, getString(R.string.email_format_wrong))
            }
            else -> {
//                val errorMsg = getString(R.string.email_taken)
                authViewModel.register(username, email, password)
                authViewModel.regMsg.observe(this) {
                    it.getContentIfNotHandled()?.let {
                        showToast(this, getString(R.string.email_taken))
                    }
                }
                authViewModel.registerUser.observe(this) { register ->
                    if (register != null) {
                        finish()
                        showToast(this, getString(R.string.success_register))
                    }
                }
            }
        }
    }
}