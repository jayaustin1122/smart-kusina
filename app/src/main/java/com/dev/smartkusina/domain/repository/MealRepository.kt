package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    suspend fun getRandomMeal(): Flow<Response<List<Meal>>>
}