package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun getAllRecipes(): Flow<Response<List<Recipes>>>
}