package com.vitaltrack.app.ui.activity_module

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.repository.StepCounterRepository
import com.vitaltrack.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val stepCounterRepository: StepCounterRepository,
    private val userRepository: UserRepository
) : ViewModel(), SensorEventListener {

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _calories = MutableStateFlow(0f)
    val calories: StateFlow<Float> = _calories.asStateFlow()

    private val _goal = MutableStateFlow(8000)
    val goal: StateFlow<Int> = _goal.asStateFlow()

    private var userWeight = 70f
    private var initialSteps = -1
    private var lastSavedDate = today()

    init {
        loadTodaySteps()
        loadUserGoal()
    }

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun loadTodaySteps() {
        viewModelScope.launch {
            stepCounterRepository.getTodaySteps().collect { entity ->
                if (entity != null) {
                    _steps.value = entity.steps
                    _calories.value = entity.calories
                }
            }
        }
    }

    private fun loadUserGoal() {
        viewModelScope.launch {
            userRepository.user.collect { user ->
                if (user != null) {
                    _goal.value = user.dailyStepsGoal
                    userWeight = user.weight
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            val currentDate = today()

            // reinicia à meia-noite
            if (currentDate != lastSavedDate) {
                initialSteps = totalSteps
                lastSavedDate = currentDate
                _steps.value = 0
            }

            // inicializa o offset na primeira leitura
            if (initialSteps == -1) {
                initialSteps = totalSteps - _steps.value
            }

            val todaySteps = totalSteps - initialSteps
            _steps.value = todaySteps
            _calories.value = todaySteps * 0.04f * userWeight / 70f

            viewModelScope.launch {
                stepCounterRepository.saveSteps(todaySteps, userWeight)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun registerSensor(sensorManager: SensorManager) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (sensor != null) {
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun unregisterSensor(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
    }
}