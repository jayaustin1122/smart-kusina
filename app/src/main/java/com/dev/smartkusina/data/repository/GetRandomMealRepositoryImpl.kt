package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.remote.mapper.toMeal
import com.dev.smartkusina.data.remote.the_meal.RandomMealService
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.repository.MealRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

            val initialBatchSize = 20
            var currentBatch = fetchMealsBatch(initialBatchSize)

            currentBatch.forEach { meal ->
                if (meal.idMeal !in seenIds) {
                    meals.add(meal)
                    seenIds.add(meal.idMeal)
                }
            }

            if (meals.isNotEmpty()) {
                emit(Response.Success(meals.toList()))
            }

            val targetMeals = 80
            val maxBatches = 8
            var batchCount = 1

            while (meals.size < targetMeals && batchCount < maxBatches) {
                val batchSize = minOf(15, (targetMeals - meals.size) * 2)
                currentBatch = fetchMealsBatch(batchSize)

                val newMeals = currentBatch.filter { meal ->
                    meal.idMeal !in seenIds
                }.take(targetMeals - meals.size)

                newMeals.forEach { meal ->
                    meals.add(meal)
                    seenIds.add(meal.idMeal)
                }

                if (newMeals.isNotEmpty()) {
                    emit(Response.Success(meals.toList()))
                }

                batchCount++
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

    private suspend fun fetchMealsBatch(batchSize: Int): List<Meal> = coroutineScope {
        val deferredMeals = (1..batchSize).map {
            async {
                try {
                    val response = randomMealService.getRandomMeal()
                    response.meals.firstOrNull()?.toMeal()
                } catch (e: Exception) {
                    null
                }
            }
        }

        deferredMeals.awaitAll().filterNotNull()
    }
}