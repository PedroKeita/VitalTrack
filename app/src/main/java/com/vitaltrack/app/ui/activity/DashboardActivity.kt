package com.vitaltrack.app.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.ActivityDashboardBinding
import com.vitaltrack.app.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCardClicks()
        observeViewModel()
    }

    private fun setupCardClicks() {
        binding.cardHydration.setOnClickListener {
            startActivity(Intent(this, HydrationActivity::class.java))
        }
        binding.cardSleep.setOnClickListener {
            startActivity(Intent(this, SleepActivity::class.java))
        }
        binding.cardActivity.setOnClickListener {
            startActivity(Intent(this, ActivityModuleActivity::class.java))
        }
        binding.cardNutrition.setOnClickListener {
            startActivity(Intent(this, NutritionActivity::class.java))
        }
        binding.cardStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.userName.collect { name ->
                    binding.tvGreeting.text = "${viewModel.greeting.value}, $name!"
                }
            }
            launch {
                viewModel.healthScore.collect { score ->
                    binding.tvHealthScore.text = "$score"
                }
            }
            launch {
                viewModel.waterTotal.collect { total ->
                    binding.tvWaterProgress.text = "${total}ml"
                }
            }
            launch {
                viewModel.waterProgress.collect { progress ->
                    binding.progressWater.progress = progress
                }
            }
            launch {
                viewModel.steps.collect { steps ->
                    binding.tvStepsProgress.text = "$steps"
                }
            }
            launch {
                viewModel.stepsProgress.collect { progress ->
                    binding.progressSteps.progress = progress
                }
            }
            launch {
                viewModel.sleepScore.collect { score ->
                    if (score > 0) binding.tvSleepScore.text = "Sono: $score"
                }
            }
            launch {
                viewModel.sleepClassification.collect { classification ->
                    if (classification.isNotEmpty())
                        binding.tvSleepClassification.text = classification
                }
            }
        }
    }
}