package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.domain.repository.MealDetailRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMealByIdUseCase @Inject constructor(
    private val mealRepository: MealDetailRepository
) {
    suspend operator fun invoke(recipeId: String): Flow<Response<MealDetails?>> {
        return mealRepository.getMealById(recipeId)
    }
}