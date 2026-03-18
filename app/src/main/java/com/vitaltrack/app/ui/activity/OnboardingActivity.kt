package com.vitaltrack.app.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.ActivityOnboardingBinding
import com.vitaltrack.app.ui.profile.ProfileViewModel
import com.vitaltrack.app.ui.profile.ProfileUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: ProfileViewModel by viewModels()

    var name: String = ""
    var age: Int = 0
    var weight: Float = 0f
    var height: Float = 0f
    var activityLevel: String = "SEDENTARY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        observeViewModel()
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state is ProfileUiState.Saved) {
                    goToDashboard()
                }
            }
        }
    }

    fun goToNextStep() {
        val current = binding.viewPager.currentItem
        if (current < 2) {
            binding.viewPager.currentItem =  current + 1
        }
    }

    fun goToPreviousStep() {
        val current = binding.viewPager.currentItem
        if (current > 0) {
            binding.viewPager.currentItem = current - 1
        }
    }

    fun finishOnboarding() {
        viewModel.saveUser(
            name = name,
            age = age,
            weight = weight,
            height = height,
            activityLevel = activityLevel
        )
    }

    private fun goToDashboard() {
        finish()
    }
}