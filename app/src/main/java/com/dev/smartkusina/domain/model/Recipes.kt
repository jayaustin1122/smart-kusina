package com.dev.smartkusina.domain.model

data class Recipes(
    val id: Long,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Long,
    val cookTimeMinutes: Long,
    val servings: Long,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Long,
    val tags: List<String>,
    val userId: Long,
    val image: String,
    val rating: Double,
    val reviewCount: Long,
    val mealType: List<String>,
)