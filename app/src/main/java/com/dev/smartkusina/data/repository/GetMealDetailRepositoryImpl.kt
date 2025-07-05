package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.remote.mapper.toDomain
import com.dev.smartkusina.data.remote.the_meal.DetailMealService
import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.domain.repository.MealDetailRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMealDetailRepositoryImpl @Inject constructor(
    private val mealService: DetailMealService
) : MealDetailRepository {

    override suspend fun getMealById(recipeId: String): Flow<Response<MealDetails?>> = flow {
        emit(Response.Loading())

        try {
            val response = mealService.getMealById(recipeId)
            val meals = response.meals
            if (meals.isNotEmpty()) {
                val meal = meals.first().toDomain()
                emit(Response.Success(meal))
            } else {
                emit(Response.Error(message = "No meal found with ID: $recipeId"))
            }
        } catch (e: Exception) {
            emit(Response.Error(message = e.localizedMessage ?: "An error occurred"))
        }
    }

}