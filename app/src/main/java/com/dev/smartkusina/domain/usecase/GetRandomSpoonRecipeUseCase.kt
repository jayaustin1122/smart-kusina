package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.domain.repository.RecipeRepository
import com.dev.smartkusina.domain.repository.SpoonRecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomSpoonRecipeUseCase @Inject constructor(
    private val spoonRecipeRepository: SpoonRecipeRepository
) {
    suspend operator fun invoke(): Flow<Response<List<SpoonRecipe>>> {
        return spoonRecipeRepository.getRandom()
    }
}