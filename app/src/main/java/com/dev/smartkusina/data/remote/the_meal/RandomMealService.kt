package com.dev.smartkusina.data.remote.the_meal

import com.dev.smartkusina.data.remote.dto.RandomMealResponse
import retrofit2.http.GET

interface RandomMealService {
    @GET("random.php")
    suspend fun getRandomMeal(): RandomMealResponse
}