package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivityNutritionBinding
import com.vitaltrack.app.ui.nutrition.NutritionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NutritionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutritionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, NutritionFragment())
            .commit()
    }
}