package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.util.Response
import kotlinx.coroutines.flow.Flow

interface SpoonRecipeRepository {
    suspend fun getRandom(): Flow<Response<List<SpoonRecipe>>>
}