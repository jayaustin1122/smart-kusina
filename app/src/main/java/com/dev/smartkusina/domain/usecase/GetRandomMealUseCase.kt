package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.repository.MealRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomMealUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(): Flow<Response<List<Meal>>> {
        return repository.getRandomMeal()
    }
}