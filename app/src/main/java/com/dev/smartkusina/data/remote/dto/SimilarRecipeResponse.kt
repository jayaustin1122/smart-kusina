package com.dev.smartkusina.data.remote.dto

data class SimilarRecipeResponse(
    val recipes: List<SimilarRecipeDto>
)

data class SimilarRecipeDto (
    val id: Long,
    val image: String,
    val imageType: String,
    val title: String,
    val readyInMinutes: Long,
    val servings: Long,
    val sourceUrl: String
)