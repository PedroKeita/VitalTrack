package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivityStatsBinding
import com.vitaltrack.app.ui.stats.StatsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, StatsFragment())
            .commit()
    }
}