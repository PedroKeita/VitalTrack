package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.MealDao
import com.vitaltrack.app.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun now(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun getTodayMeals(): Flow<List<MealEntity>> = mealDao.getByDate(today())

    fun getTodayCalories(): Flow<Int?> = mealDao.getTotalCaloriesByDate(today())

    suspend fun saveMeal(
        description: String,
        category: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) {
        mealDao.insert(
            MealEntity(
                description = description,
                category = category,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                date = today(),
                time = now()
            )
        )
    }

    suspend fun delete(meal: MealEntity) = mealDao.delete(meal)
}