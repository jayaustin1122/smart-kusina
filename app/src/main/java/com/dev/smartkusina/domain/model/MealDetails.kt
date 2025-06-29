package com.dev.smartkusina.domain.model

data class MealDetails(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String,
    val tags: List<String>,
    val youtubeUrl: String?,
    val ingredients: List<Ingredient>,
    val sourceUrl: String?
)

data class Ingredient(
    val name: String,
    val measure: String
)
