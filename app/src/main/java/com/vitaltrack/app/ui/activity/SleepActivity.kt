package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivitySleepBinding
import com.vitaltrack.app.ui.sleep.SleepFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SleepActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySleepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, SleepFragment())
            .commit()
    }
}