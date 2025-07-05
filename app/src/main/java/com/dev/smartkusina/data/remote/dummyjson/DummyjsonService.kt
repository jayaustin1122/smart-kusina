package com.dev.smartkusina.data.remote.dummyjson

import com.dev.smartkusina.data.remote.dto.RecipesResponse
import retrofit2.http.GET

interface DummyjsonService {
    @GET("recipes")
    suspend fun getAllRecipes() : RecipesResponse
}