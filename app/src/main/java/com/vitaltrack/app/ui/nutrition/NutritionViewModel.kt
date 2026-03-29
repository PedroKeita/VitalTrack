package com.vitaltrack.app.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrack.app.data.local.entity.MealEntity
import com.vitaltrack.app.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _meals = MutableStateFlow<List<MealEntity>>(emptyList())
    val meals: StateFlow<List<MealEntity>> = _meals.asStateFlow()

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories: StateFlow<Int> = _totalCalories.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                mealRepository.getTodayMeals().collect { _meals.value = it }
            }
            launch {
                mealRepository.getTodayCalories().collect {
                    _totalCalories.value = it ?: 0
                }
            }
        }
    }

    fun saveMeal(
        description: String,
        category: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) {
        viewModelScope.launch {
            mealRepository.saveMeal(description, category, calories, protein, carbs, fat)
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealRepository.update(meal)
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealRepository.delete(meal)
        }
    }

    fun parseScannedText(text: String): ScannedNutrition {
        var calories = 0
        var protein = 0f
        var carbs = 0f
        var fat = 0f

        val lines = text.lines()

        for (line in lines) {
            val lower = line.lowercase().trim()


            val cleaned = lower
                .replace(",", ".")
                .replace("kcal", " kcal")
                .replace("kj", " kj")
                .replace("O", "0")
                .replace("o", "0")
                .replace("l", "1")
                .replace("I", "1")
                // corrige "109" → "10" quando provavelmente era "10g"
                .replace(Regex("(\\d)9(?=\\s|$)"), "$1")


            val matches = Regex("(\\d+(?:\\.\\d+)?)\\s*(kcal|kj|g|mg)?")
                .findAll(cleaned)
                .mapNotNull { match ->
                    val value = match.groupValues[1].toFloatOrNull()
                    val unit = match.groupValues[2]

                    if (value != null) Pair(value, unit) else null
                }
                .toList()

            if (matches.isEmpty()) continue


            if (
                lower.contains("valor energ") ||
                lower.contains("energia") ||
                lower.contains("kcal")
            ) {
                val kcal = matches.firstOrNull {
                    it.second == "kcal" && it.first in 10f..2000f
                }?.first

                val fallback = matches.firstOrNull {
                    it.first in 10f..2000f
                }?.first

                calories = (kcal ?: fallback ?: calories.toFloat()).toInt()
            }


            if (lower.contains("prote")) {
                val value = matches.firstOrNull {
                    (it.second == "g" || it.second.isEmpty()) &&
                            it.first in 0.1f..200f
                }?.first

                if (value != null) protein = value
            }


            if (
                (lower.contains("carboidrato") || lower.contains("carb")) &&
                !lower.contains("açucar") &&
                !lower.contains("adicion")
            ) {
                val value = matches.firstOrNull {
                    (it.second == "g" || it.second.isEmpty()) &&
                            it.first in 0.1f..500f
                }?.first

                if (value != null && carbs == 0f) carbs = value
            }


            if (
                (lower.contains("gordura") || lower.contains("lipid")) &&
                !lower.contains("satur") &&
                !lower.contains("trans") &&
                !lower.contains("mono") &&
                !lower.contains("poli")
            ) {
                val value = matches.firstOrNull {
                    (it.second == "g" || it.second.isEmpty()) &&
                            it.first in 0.1f..200f
                }?.first

                if (value != null && fat == 0f) fat = value
            }
        }

        if (calories == 0 && (protein > 0 || carbs > 0 || fat > 0)) {
            calories = ((protein * 4) + (carbs * 4) + (fat * 9)).toInt()
        }

        return ScannedNutrition(calories, protein, carbs, fat)
    }
}

data class ScannedNutrition(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)