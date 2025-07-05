package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.repository.RecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): Flow<Response<List<Recipes>>> {
        return recipeRepository.getAllRecipes()
    }
}