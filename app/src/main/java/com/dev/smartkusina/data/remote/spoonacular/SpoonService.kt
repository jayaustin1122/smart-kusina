package com.dev.smartkusina.data.remote.spoonacular

import com.dev.smartkusina.data.remote.dto.SimilarRecipeResponse
import com.dev.smartkusina.data.remote.dto.SpoonRecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonService {

    @GET("recipes/random?apiKey=d4c1f3a86d744abebdd7cc6cc0f3e70a&number=20")
    suspend fun getRandomRecipes(): SpoonRecipeResponse

    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10
    ): SimilarRecipeResponse

}