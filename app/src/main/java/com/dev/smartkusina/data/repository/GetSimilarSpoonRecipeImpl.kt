package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.remote.mapper.toDomain
import com.dev.smartkusina.data.remote.spoonacular.SpoonService
import com.dev.smartkusina.domain.model.SimilarRecipe
import com.dev.smartkusina.domain.repository.SimilarSpoonRecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSimilarSpoonRecipeImpl @Inject constructor(
    private val spoonService: SpoonService,
    private val apiKey: String
) : SimilarSpoonRecipeRepository {

    override suspend fun getSimilar(
        recipeId: Int,
        number: Int
    ): Flow<Response<List<SimilarRecipe>>> =
        flow {
            emit(Response.Loading())
            try {
                val response = spoonService.getSimilarRecipes(recipeId, apiKey, number)
                val recipesList = response.recipes.map { it.toDomain() }
                emit(Response.Success(recipesList))
            } catch (e: Exception) {
                emit(Response.Error(message = e.localizedMessage ?: "An error occurred"))
            }
        }
}

