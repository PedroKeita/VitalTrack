package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivityModuleBinding
import com.vitaltrack.app.ui.activity_module.ActivityFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityModuleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, ActivityFragment())
            .commit()
    }
}