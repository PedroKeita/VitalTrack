package com.vitaltrack.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.repository.SleepRepository
import com.vitaltrack.app.data.repository.StepCounterRepository
import com.vitaltrack.app.data.repository.UserRepository
import com.vitaltrack.app.data.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val waterIntakeRepository: WaterIntakeRepository,
    private val stepCounterRepository: StepCounterRepository,
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    private val _waterTotal = MutableStateFlow(0)
    val waterTotal: StateFlow<Int> = _waterTotal.asStateFlow()

    private val _waterGoal = MutableStateFlow(2000)
    val waterGoal: StateFlow<Int> = _waterGoal.asStateFlow()

    private val _waterProgress = MutableStateFlow(0)
    val waterProgress: StateFlow<Int> = _waterProgress.asStateFlow()

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _stepsGoal = MutableStateFlow(8000)
    val stepsGoal: StateFlow<Int> = _stepsGoal.asStateFlow()

    private val _stepsProgress = MutableStateFlow(0)
    val stepsProgress: StateFlow<Int> = _stepsProgress.asStateFlow()

    private val _sleepScore = MutableStateFlow(0)
    val sleepScore: StateFlow<Int> = _sleepScore.asStateFlow()

    private val _sleepClassification = MutableStateFlow("")
    val sleepClassification: StateFlow<String> = _sleepClassification.asStateFlow()

    private val _healthScore = MutableStateFlow(0)
    val healthScore: StateFlow<Int> = _healthScore.asStateFlow()

    init {
        loadData()
        setGreeting()
    }

    private fun setGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        _greeting.value = when {
            hour < 12 -> "Bom dia"
            hour < 18 -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // usuário
            launch {
                userRepository.user.collect { user ->
                    _userName.value = user?.name ?: ""
                    _waterGoal.value = user?.dailyWaterGoalMl ?: 2000
                    _stepsGoal.value = user?.dailyStepsGoal ?: 8000
                    calculateHealthScore()
                }
            }

            // hidratação
            launch {
                waterIntakeRepository.getTodayTotal().collect { total ->
                    _waterTotal.value = total ?: 0
                    val progress = if (_waterGoal.value > 0)
                        ((_waterTotal.value * 100) / _waterGoal.value).coerceAtMost(100)
                    else 0
                    _waterProgress.value = progress
                    calculateHealthScore()
                }
            }

            // passos
            launch {
                stepCounterRepository.getTodaySteps().collect { entity ->
                    _steps.value = entity?.steps ?: 0
                    val progress = if (_stepsGoal.value > 0)
                        ((_steps.value * 100) / _stepsGoal.value).coerceAtMost(100)
                    else 0
                    _stepsProgress.value = progress
                    calculateHealthScore()
                }
            }

            // sono
            launch {
                sleepRepository.getLatest().collect { sleep ->
                    if (sleep?.endTime != null) {
                        val score = sleepRepository.calculateScore(sleep, null)
                        _sleepScore.value = score
                        _sleepClassification.value = sleepRepository.getClassification(score)
                        calculateHealthScore()
                    }
                }
            }
        }
    }

    private fun calculateHealthScore() {
        val waterScore = (_waterProgress.value * 0.35f).toInt()
        val stepsScore = (_stepsProgress.value * 0.35f).toInt()
        val sleepScore = (_sleepScore.value * 0.30f).toInt()
        _healthScore.value = (waterScore + stepsScore + sleepScore).coerceIn(0, 100)
    }
}