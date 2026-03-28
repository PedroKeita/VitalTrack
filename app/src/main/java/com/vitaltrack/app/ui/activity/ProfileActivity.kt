package com.vitaltrack.app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vitaltrack.app.databinding.ActivityProfileBinding
import com.vitaltrack.app.ui.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // carrega o ProfileFragment
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, ProfileFragment())
            .commit()
    }
}