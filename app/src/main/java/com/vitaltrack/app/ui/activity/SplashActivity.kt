package com.vitaltrack.app.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.ActivitySplashBinding
import com.vitaltrack.app.ui.profile.ProfileViewModel
import com.vitaltrack.app.ui.profile.ProfileUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserProfile()
    }

    private fun checkUserProfile() {
        lifecycleScope.launch {
            delay(2000)
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProfileUiState.Success -> goToDashboard()
                    is ProfileUiState.Empty -> goToOnboarding()
                    else -> {}
                }
            }
        }
    }

    private fun goToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}