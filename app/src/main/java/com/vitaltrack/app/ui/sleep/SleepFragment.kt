package com.vitaltrack.app.ui.sleep

import android.app.AlertDialog
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vitaltrack.app.databinding.FragmentSleepBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SleepFragment : Fragment() {

    private var _binding: FragmentSleepBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SleepViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private val historyAdapter = SleepHistoryAdapter { viewModel.delete(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        binding.btnSleep.setOnClickListener {
            viewModel.startSleep()
            viewModel.registerSensor(sensorManager)
        }

        binding.btnWakeUp.setOnClickListener {
            viewModel.endSleep()
            viewModel.unregisterSensor(sensorManager)
        }

        binding.btnManual.setOnClickListener {
            showManualDialog()
        }
    }

    private fun showManualDialog() {
        val layout = android.widget.LinearLayout(requireContext())
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(48, 24, 48, 0)

        val etStart = EditText(requireContext()).apply { hint = "Início (ex: 22:30)" }
        val etEnd = EditText(requireContext()).apply { hint = "Fim (ex: 06:30)" }

        layout.addView(etStart)
        layout.addView(etEnd)

        AlertDialog.Builder(requireContext())
            .setTitle("Inserir horários manualmente")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val start = etStart.text.toString().trim()
                val end = etEnd.text.toString().trim()
                if (start.isNotEmpty() && end.isNotEmpty()) {
                    viewModel.saveSleepManual(start, end)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SleepUiState.Idle -> {
                            binding.btnSleep.visibility = View.VISIBLE
                            binding.btnWakeUp.visibility = View.GONE
                            binding.tvStatus.text = "Pronto para dormir?"
                            binding.tvDuration.visibility = View.GONE
                        }
                        is SleepUiState.Sleeping -> {
                            binding.btnSleep.visibility = View.GONE
                            binding.btnWakeUp.visibility = View.VISIBLE
                            binding.tvStatus.text = "Dormindo desde ${state.startTime}"
                            binding.tvDuration.visibility = View.GONE
                        }
                        is SleepUiState.Done -> {
                            binding.btnSleep.visibility = View.VISIBLE
                            binding.btnWakeUp.visibility = View.GONE
                            val hours = state.durationMinutes / 60
                            val mins = state.durationMinutes % 60
                            binding.tvStatus.text = "${state.startTime} → ${state.endTime}"
                            binding.tvDuration.visibility = View.VISIBLE
                            binding.tvDuration.text = "Duração: ${hours}h ${mins}min"
                        }
                    }
                }
            }
            launch {
                viewModel.score.collect { score ->
                    if (score > 0) {
                        binding.tvScore.visibility = View.VISIBLE
                        binding.tvScore.text = "$score"
                    }
                }
            }
            launch {
                viewModel.classification.collect { classification ->
                    if (classification.isNotEmpty()) {
                        binding.tvClassification.visibility = View.VISIBLE
                        binding.tvClassification.text = classification
                    }
                }
            }
            launch {
                viewModel.tip.collect { tip ->
                    if (tip.isNotEmpty()) {
                        binding.tvTip.visibility = View.VISIBLE
                        binding.tvTip.text = tip
                    }
                }
            }
            launch {
                viewModel.latest.collect { sleep ->
                    if (sleep?.endTime != null) {
                        val hours = (sleep.durationMinutes ?: 0) / 60
                        val mins = (sleep.durationMinutes ?: 0) % 60
                        binding.tvLastSleep.text =
                            "Última noite: ${sleep.startTime} → ${sleep.endTime} (${hours}h ${mins}min)"
                    }
                }
            }
            launch {
                viewModel.history.collect { history ->
                    historyAdapter.submitList(history)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unregisterSensor(sensorManager)
        _binding = null
    }
}