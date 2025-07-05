package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.remote.dummyjson.DummyjsonService
import com.dev.smartkusina.data.remote.mapper.toDomain
import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.repository.RecipeRepository
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllRecipesFromDummyJsonImpl @Inject constructor(
    private val dummyjsonService: DummyjsonService
) : RecipeRepository {

    override suspend fun getAllRecipes(): Flow<Response<List<Recipes>>> = flow {
        emit(Response.Loading())
        try {
            val response = dummyjsonService.getAllRecipes()
            val recipesList = response.recipes.map { it.toDomain() }
            emit(Response.Success(recipesList))
        } catch (e: Exception) {
            emit(Response.Error(message = e.localizedMessage ?: "An error occurred"))
        }
    }
}