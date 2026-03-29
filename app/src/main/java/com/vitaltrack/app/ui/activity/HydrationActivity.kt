package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivityHydrationBinding
import com.vitaltrack.app.ui.hydration.HydrationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HydrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHydrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHydrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HydrationFragment())
            .commit()
    }
}