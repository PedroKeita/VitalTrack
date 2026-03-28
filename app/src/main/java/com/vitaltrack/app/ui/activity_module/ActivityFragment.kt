package com.vitaltrack.app.ui.activity_module

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.FragmentActivityBinding
import com.vitaltrack.app.service.StepCounterService
import com.vitaltrack.app.ui.activity.MapActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StepCounterViewModel by viewModels()
    private lateinit var sensorManager: SensorManager

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startStepCounter()
        else binding.tvSteps.text = "Permissão negada"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        checkPermissionAndStart()
        observeViewModel()
    }

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startStepCounter()
            } else {
                permissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            startStepCounter()
        }
    }

    private fun startStepCounter() {
        viewModel.registerSensor(sensorManager)


        // inicia o service em background
        val intent = Intent(requireContext(), StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }

        binding.btnStartOutdoor.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.steps.collect { steps ->
                    binding.tvSteps.text = "$steps"
                }
            }
            launch {
                viewModel.calories.collect { calories ->
                    binding.tvCalories.text = "%.0f kcal".format(calories)
                }
            }
            launch {
                viewModel.goal.collect { goal ->
                    binding.tvGoal.text = "Meta: $goal passos"
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterSensor(sensorManager)
    }

    override fun onResume() {
        super.onResume()
        checkPermissionAndStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}