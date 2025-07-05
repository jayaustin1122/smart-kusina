package com.dev.smartkusina.data.repository

import android.util.Log
import com.dev.smartkusina.data.remote.mapper.toDomain
import com.dev.smartkusina.data.remote.spoonacular.SpoonService
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.domain.repository.SpoonRecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRandomSpoonRecipeImpl @Inject constructor(
    private val spoonService: SpoonService
) : SpoonRecipeRepository {
    override suspend fun getRandom(): Flow<Response<List<SpoonRecipe>>> = flow{
        emit(Response.Loading())
        try {
            val response = spoonService.getRandomRecipes()
            val recipesList = response.recipes.map { it.toDomain() }
            emit(Response.Success(recipesList))
            Log.d("GetRandomSpoonRecipeImpl", "getRandom: $recipesList")
        } catch (e: Exception) {
            emit(Response.Error(message = e.localizedMessage ?: "An error occurred"))
        }
    }
}