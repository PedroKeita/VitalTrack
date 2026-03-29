package com.vitaltrack.app.ui.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.local.entity.WaterIntakeEntity
import com.vitaltrack.app.data.repository.UserRepository
import com.vitaltrack.app.data.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val waterIntakeRepository: WaterIntakeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _totalMl = MutableStateFlow(0)
    val totalMl: StateFlow<Int> = _totalMl.asStateFlow()

    private val _goalMl = MutableStateFlow(2000)
    val goalMl: StateFlow<Int> = _goalMl.asStateFlow()

    private val _intakes = MutableStateFlow<List<WaterIntakeEntity>>(emptyList())
    val intakes: StateFlow<List<WaterIntakeEntity>> = _intakes.asStateFlow()

    private val _goalReached = MutableStateFlow(false)
    val goalReached: StateFlow<Boolean> = _goalReached.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                waterIntakeRepository.getTodayTotal().collect { total ->
                    _totalMl.value = total ?: 0
                    _goalReached.value = (_totalMl.value >= _goalMl.value)
                }
            }
            launch {
                waterIntakeRepository.getTodayIntakes().collect {
                    _intakes.value = it
                }
            }
            launch {
                userRepository.user.collect { user ->
                    _goalMl.value = user?.dailyWaterGoalMl ?: 2000
                }
            }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterIntakeRepository.addWater(amountMl)
        }
    }

    fun deleteIntake(water: WaterIntakeEntity) {
        viewModelScope.launch {
            waterIntakeRepository.delete(water)
        }
    }
}