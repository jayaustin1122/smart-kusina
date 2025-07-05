package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.domain.model.SimilarRecipe
import com.dev.smartkusina.domain.repository.SimilarSpoonRecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSimilarRecipesUseCase @Inject constructor(
    private val repository: SimilarSpoonRecipeRepository
) {
    suspend operator fun invoke(recipeId: Int, number: Int = 10): Flow<Response<List<SimilarRecipe>>> {
        return repository.getSimilar(recipeId, number)
    }
}