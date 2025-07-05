package com.dev.smartkusina.domain.model

data class SimilarRecipe (
    val id: Long,
    val image: String,
    val imageType: String,
    val title: String,
    val readyInMinutes: Long,
    val servings: Long,
    val sourceUrl: String,
)