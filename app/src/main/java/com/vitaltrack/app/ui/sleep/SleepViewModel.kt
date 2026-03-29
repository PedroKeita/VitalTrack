package com.vitaltrack.app.ui.sleep

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.local.entity.SleepEntity
import com.vitaltrack.app.data.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
) : ViewModel(), SensorEventListener {

    private val _uiState = MutableStateFlow<SleepUiState>(SleepUiState.Idle)
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    private val _latest = MutableStateFlow<SleepEntity?>(null)
    val latest: StateFlow<SleepEntity?> = _latest.asStateFlow()

    private val _history = MutableStateFlow<List<SleepWithScore>>(emptyList())
    val history: StateFlow<List<SleepWithScore>> = _history.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _tip = MutableStateFlow("")
    val tip: StateFlow<String> = _tip.asStateFlow()

    private val _classification = MutableStateFlow("")
    val classification: StateFlow<String> = _classification.asStateFlow()

    private var ongoingSleep: SleepEntity? = null
    private var agitationThreshold = 2f

    init {
        loadData()
        checkOngoing()
    }



    private fun loadData() {
        viewModelScope.launch {
            launch {
                sleepRepository.getLatest().collect { sleep ->
                    _latest.value = sleep
                    if (sleep?.endTime != null) {
                        val lastTwo = sleepRepository.getLastTwo()
                        val previous = if (lastTwo.size >= 2) lastTwo[1] else null
                        val s = sleepRepository.calculateScore(sleep, previous)
                        _score.value = s
                        _classification.value = sleepRepository.getClassification(s)
                        _tip.value = sleepRepository.getTip(s)
                    }
                }
            }
            launch {
                sleepRepository.getLast7Days().collect { list ->
                    _history.value = list
                        .filter { it.endTime != null }
                        .map { sleep ->
                            val s = sleepRepository.calculateScore(sleep, null)
                            SleepWithScore(sleep, s, sleepRepository.getClassification(s))
                        }
                }
            }
        }
    }

    private fun checkOngoing() {
        viewModelScope.launch {
            val ongoing = sleepRepository.getOngoing()
            if (ongoing != null) {
                ongoingSleep = ongoing
                _uiState.value = SleepUiState.Sleeping(ongoing.startTime)
            }
        }
    }

    fun startSleep() {
        viewModelScope.launch {
            val id = sleepRepository.startSleep()
            ongoingSleep = sleepRepository.getOngoing()
            _uiState.value = SleepUiState.Sleeping(ongoingSleep?.startTime ?: "")
        }
    }


    fun endSleep() {
        viewModelScope.launch {
            val ongoing = ongoingSleep ?: return@launch
            val result = sleepRepository.endSleep(ongoing)
            ongoingSleep = null
            _uiState.value = SleepUiState.Done(
                startTime = result.startTime,
                endTime = result.endTime ?: "",
                durationMinutes = result.durationMinutes ?: 0
            )
        }
    }


    fun saveSleepManual(startTime: String, endTime: String) {
        viewModelScope.launch {
            val sleep = SleepEntity(
                date = today(),
                startTime = startTime,
                endTime = endTime,
                durationMinutes = calculateDuration(startTime, endTime)
            )
            sleepRepository.saveComplete(sleep)
            _uiState.value = SleepUiState.Done(
                startTime = startTime,
                endTime = endTime,
                durationMinutes = sleep.durationMinutes ?: 0
            )
            // força recarregar o histórico
            loadData()
        }
    }

    private fun today(): String =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

    private fun calculateDuration(start: String, end: String): Int {
        return try {
            val fmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val startDate = fmt.parse(start) ?: return 0
            val endDate = fmt.parse(end) ?: return 0
            var diff = ((endDate.time - startDate.time) / 60000).toInt()
            if (diff < 0) diff += 24 * 60
            diff
        } catch (e: Exception) { 0 }
    }

    fun registerSensor(sensorManager: SensorManager) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterSensor(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
    }

    fun delete(item: SleepWithScore) {
        viewModelScope.launch {
            sleepRepository.delete(item.sleep)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // detecta agitação acima do threshold
            if (magnitude > 12f && ongoingSleep != null) {
                viewModelScope.launch {
                    ongoingSleep?.let {
                        sleepRepository.incrementAgitation(it)
                        ongoingSleep = sleepRepository.getOngoing()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

sealed class SleepUiState {
    object Idle : SleepUiState()
    data class Sleeping(val startTime: String) : SleepUiState()
    data class Done(
        val startTime: String,
        val endTime: String,
        val durationMinutes: Int
    ) : SleepUiState()
}