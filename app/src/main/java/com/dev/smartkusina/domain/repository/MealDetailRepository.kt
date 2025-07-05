package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow

interface MealDetailRepository {
    suspend fun getMealById(recipeId: String): Flow<Response<MealDetails?>>
}