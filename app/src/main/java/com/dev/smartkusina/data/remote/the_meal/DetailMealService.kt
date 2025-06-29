package com.dev.smartkusina.data.remote.the_meal

import com.dev.smartkusina.data.remote.dto.MealDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DetailMealService {
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") recipeId: String): MealDetailResponse
}