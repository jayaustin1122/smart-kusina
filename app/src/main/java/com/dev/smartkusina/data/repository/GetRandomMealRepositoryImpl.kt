package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.remote.mapper.toMeal
import com.dev.smartkusina.data.remote.the_meal.RandomMealService
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.repository.MealRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRandomMealRepositoryImpl @Inject constructor(
    private val randomMealService: RandomMealService
) : MealRepository {
    override suspend fun getRandomMeal(): Flow<Response<List<Meal>>> = flow {
        emit(Response.Loading())

        try {
            val meals = mutableListOf<Meal>()
            val seenIds = mutableSetOf<String>()

            var attempts = 0
            val maxMeals = 80
            val maxAttempts = 200

            while (meals.size < maxMeals && attempts < maxAttempts) {
                val response = randomMealService.getRandomMeal()
                val meal = response.meals.firstOrNull()?.toMeal()

                if (meal != null && meal.idMeal !in seenIds) {
                    meals.add(meal)
                    seenIds.add(meal.idMeal)
                }

                attempts++
            }

            if (meals.isNotEmpty()) {
                emit(Response.Success(meals))
            } else {
                emit(Response.Error(message = "No unique meals retrieved"))
            }

        } catch (e: Exception) {
            emit(Response.Error(message = e.localizedMessage ?: "An error occurred"))
        }
    }
}