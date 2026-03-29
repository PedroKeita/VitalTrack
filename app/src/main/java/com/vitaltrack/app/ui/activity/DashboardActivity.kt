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

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.userName.collect { name ->
                    binding.tvGreeting.text = "${viewModel.greeting.value}, $name!"
                }
            }
        }

        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.cardActivity.setOnClickListener {
            startActivity(Intent(this, ActivityModuleActivity::class.java))
        }

        binding.cardHydration.setOnClickListener {
            startActivity(Intent(this, HydrationActivity::class.java))
        }
    }


}