package com.vitaltrack.app.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.vitaltrack.app.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggle()
        setupCharts()
        observeViewModel()
    }

    private fun setupToggle() {
        binding.togglePeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val days = if (checkedId == binding.btn7days.id) 7 else 30
                viewModel.loadData(days)
            }
        }
        binding.btn7days.isChecked = true
    }

    private fun setupCharts() {
        listOf(binding.chartWater, binding.chartSleep).forEach { chart ->
            chart.apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisRight.isEnabled = false
                axisLeft.setDrawGridLines(false)
            }
        }

        binding.chartSteps.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(false)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.waterData.collect { data ->
                    if (data.isNotEmpty()) updateLineChart(
                        binding.chartWater, data,
                        Color.parseColor("#2196F3")
                    )
                }
            }
            launch {
                viewModel.stepsData.collect { data ->
                    if (data.isNotEmpty()) updateBarChart(
                        binding.chartSteps, data,
                        Color.parseColor("#10B981")
                    )
                }
            }
            launch {
                viewModel.sleepData.collect { data ->
                    if (data.isNotEmpty()) updateLineChart(
                        binding.chartSleep, data,
                        Color.parseColor("#7C3AED")
                    )
                }
            }
            launch {
                viewModel.waterAvg.collect { avg ->
                    binding.tvWaterAvg.text = "Média: %.0f ml".format(avg)
                }
            }
            launch {
                viewModel.stepsAvg.collect { avg ->
                    binding.tvStepsAvg.text = "Média: %.0f passos".format(avg)
                }
            }
            launch {
                viewModel.sleepAvg.collect { avg ->
                    binding.tvSleepAvg.text = "Média: %.0f pts".format(avg)
                }
            }
            launch {
                viewModel.waterTrend.collect { binding.tvWaterTrend.text = it }
            }
            launch {
                viewModel.stepsTrend.collect { binding.tvStepsTrend.text = it }
            }
            launch {
                viewModel.sleepTrend.collect { binding.tvSleepTrend.text = it }
            }
        }
    }

    private fun updateLineChart(chart: LineChart, data: List<Float>, color: Int) {
        val entries = data.mapIndexed { i, v -> Entry(i.toFloat(), v) }
        val dataSet = LineDataSet(entries, "").apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun updateBarChart(chart: BarChart, data: List<Float>, color: Int) {
        val entries = data.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "").apply {
            this.color = color
            setDrawValues(false)
        }
        chart.data = BarData(dataSet)
        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}