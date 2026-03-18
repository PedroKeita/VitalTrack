package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.UserDao
import com.vitaltrack.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    val user: Flow<UserEntity?> = userDao.getUser()

    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun userExists(): Boolean {
        return userDao.userExists() > 0
    }

    suspend fun getUserOnce(): UserEntity? {
        return userDao.getUserOnce()
    }

    fun calculateWaterGoal(weight: Float, activityLevel: String): Int {
        val base = weight * 35f
        val multiplier = when (activityLevel) {
            "LIGHT"       -> 1.1f
            "MODERATE"    -> 1.2f
            "ACTIVE"      -> 1.3f
            "VERY_ACTIVE" -> 1.5f
            else          -> 1.0f
        }
        return (base * multiplier).toInt()
    }

    fun calculateStepsGoal(activityLevel: String): Int {
        return when (activityLevel) {
            "SEDENTARY"   -> 5000
            "LIGHT"       -> 7500
            "MODERATE"    -> 10000
            "ACTIVE"      -> 12000
            "VERY_ACTIVE" -> 15000
            else          -> 8000
        }
    }


}