package com.vitaltrack.app.ui.hydration

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.FragmentHydrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HydrationFragment : Fragment() {

    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HydrationViewModel by viewModels()
    private val adapter = WaterIntakeAdapter { viewModel.deleteIntake(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvIntakes.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = this@HydrationFragment.adapter
        }

        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        binding.btn200ml.setOnClickListener { viewModel.addWater(200) }
        binding.btn300ml.setOnClickListener { viewModel.addWater(300) }
        binding.btn500ml.setOnClickListener { viewModel.addWater(500) }

        binding.btnCustom.setOnClickListener {
            val input = EditText(requireContext()).apply {
                hint = "Entre 50 e 2000ml"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Volume personalizado")
                .setView(input)
                .setPositiveButton("Adicionar") { _, _ ->
                    val amount = input.text.toString().toIntOrNull() ?: 0
                    if (amount in 50..2000) {
                        viewModel.addWater(amount)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.totalMl.collect { total ->
                    binding.tvTotal.text = "${total}ml"
                    val goal = viewModel.goalMl.value
                    val progress = if (goal > 0) (total * 100 / goal) else 0
                    binding.progressWater.progress = progress.coerceAtMost(100)
                    binding.tvProgress.text = "$progress%"
                }
            }
            launch {
                viewModel.goalMl.collect { goal ->
                    binding.tvGoal.text = "Meta: ${goal}ml"
                }
            }
            launch {
                viewModel.intakes.collect {
                    adapter.submitList(it)
                }
            }
            launch {
                viewModel.goalReached.collect { reached ->
                    if (reached) showCelebration()
                }
            }
        }
    }

    private fun showCelebration() {
        binding.tvCelebration.visibility = View.VISIBLE
        binding.tvCelebration.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                binding.tvCelebration.animate()
                    .alpha(0f)
                    .setStartDelay(2000)
                    .setDuration(500)
                    .withEndAction {
                        binding.tvCelebration.visibility = View.GONE
                    }
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}