package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.SimilarRecipe
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow

interface SimilarSpoonRecipeRepository {
    suspend fun getSimilar(recipeId: Int, number: Int = 10 ): Flow<Response<List<SimilarRecipe>>>
}