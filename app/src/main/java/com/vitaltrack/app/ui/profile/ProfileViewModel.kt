package com.vitaltrack.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.local.entity.UserEntity
import com.vitaltrack.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado UI
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userRepository.user.collect { user ->
                if (user != null) {
                    _uiState.value = ProfileUiState.Success(user)
                } else {
                    _uiState.value = ProfileUiState.Empty
                }
            }
        }
    }

    fun saveUser(
        name: String,
        age: Int,
        weight: Float,
        height: Float,
        activityLevel: String
    ) {
        viewModelScope.launch {
            val waterGoal = userRepository.calculateWaterGoal(weight, activityLevel)
            val stepsGoal = userRepository.calculateStepsGoal(activityLevel)

            val user = UserEntity(
                name = name,
                age = age,
                weight = weight,
                height = height,
                activityLevel = activityLevel,
                dailyWaterGoalMl = waterGoal,
                dailyStepsGoal = stepsGoal,
                dailySleepGoalHours = 8f
            )
            userRepository.saveUser(user)
            _uiState.value = ProfileUiState.Saved
        }
    }

    fun updateUser(
        name: String,
        age: Int,
        weight: Float,
        height: Float,
        activityLevel: String
    ) {
        viewModelScope.launch {
            val current = userRepository.getUserOnce() ?: return@launch
            val waterGoal = userRepository.calculateWaterGoal(weight, activityLevel)
            val stepsGoal = userRepository.calculateStepsGoal(activityLevel)

            val updated = current.copy(
                name = name,
                age = age,
                weight = weight,
                height = height,
                activityLevel = activityLevel,
                dailyWaterGoalMl = waterGoal,
                dailyStepsGoal = stepsGoal,
                updatedAt = System.currentTimeMillis()
            )
            userRepository.updateUser(updated)
            _uiState.value = ProfileUiState.Saved
        }
    }
}

// estados possíveis da tela
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Empty : ProfileUiState()
    object Saved : ProfileUiState()
    data class Success(val user: UserEntity) : ProfileUiState()
}