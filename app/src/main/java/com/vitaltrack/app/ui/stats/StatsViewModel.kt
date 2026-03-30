package com.vitaltrack.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.repository.SleepRepository
import com.vitaltrack.app.data.repository.StepCounterRepository
import com.vitaltrack.app.data.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val waterIntakeRepository: WaterIntakeRepository,
    private val stepCounterRepository: StepCounterRepository,
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val _waterData = MutableStateFlow<List<Float>>(emptyList())
    val waterData: StateFlow<List<Float>> = _waterData.asStateFlow()

    private val _stepsData = MutableStateFlow<List<Float>>(emptyList())
    val stepsData: StateFlow<List<Float>> = _stepsData.asStateFlow()

    private val _sleepData = MutableStateFlow<List<Float>>(emptyList())
    val sleepData: StateFlow<List<Float>> = _sleepData.asStateFlow()

    private val _waterAvg = MutableStateFlow(0f)
    val waterAvg: StateFlow<Float> = _waterAvg.asStateFlow()

    private val _stepsAvg = MutableStateFlow(0f)
    val stepsAvg: StateFlow<Float> = _stepsAvg.asStateFlow()

    private val _sleepAvg = MutableStateFlow(0f)
    val sleepAvg: StateFlow<Float> = _sleepAvg.asStateFlow()

    private val _waterTrend = MutableStateFlow("")
    val waterTrend: StateFlow<String> = _waterTrend.asStateFlow()

    private val _stepsTrend = MutableStateFlow("")
    val stepsTrend: StateFlow<String> = _stepsTrend.asStateFlow()

    private val _sleepTrend = MutableStateFlow("")
    val sleepTrend: StateFlow<String> = _sleepTrend.asStateFlow()

    private var currentDays = 7

    init {
        loadData(7)
    }

    fun loadData(days: Int) {
        currentDays = days
        viewModelScope.launch {
            launch { loadWaterData(days) }
            launch { loadStepsData(days) }
            launch { loadSleepData(days) }
        }
    }

    private suspend fun loadWaterData(days: Int) {
        val data = waterIntakeRepository.getLast(days)
            .map { it.total.toFloat() }
            .reversed()
        _waterData.value = data
        _waterAvg.value = if (data.isNotEmpty()) data.average().toFloat() else 0f
        _waterTrend.value = calculateTrend(data)
    }

    private suspend fun loadStepsData(days: Int) {
        val data = stepCounterRepository.getLast(days)
            .map { it.steps.toFloat() }
            .reversed()
        _stepsData.value = data
        _stepsAvg.value = if (data.isNotEmpty()) data.average().toFloat() else 0f
        _stepsTrend.value = calculateTrend(data)
    }

    private suspend fun loadSleepData(days: Int) {
        val data = sleepRepository.getLast(days)
            .map { sleepRepository.calculateScore(it, null).toFloat() }
            .reversed()
        _sleepData.value = data
        _sleepAvg.value = if (data.isNotEmpty()) data.average().toFloat() else 0f
        _sleepTrend.value = calculateTrend(data)
    }

    private fun calculateTrend(data: List<Float>): String {
        if (data.size < 2) return "→ Estável"
        val firstHalf = data.take(data.size / 2).average()
        val secondHalf = data.takeLast(data.size / 2).average()
        val diff = secondHalf - firstHalf
        return when {
            diff > firstHalf * 0.05 -> "↑ Melhorando"
            diff < -firstHalf * 0.05 -> "↓ Piorando"
            else -> "→ Estável"
        }
    }
}