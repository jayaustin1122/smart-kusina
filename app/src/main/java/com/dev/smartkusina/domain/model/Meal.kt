package com.dev.smartkusina.domain.model

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strYoutube: String?,
    val ingredients: List<String?>,
    val measures: List<String?>
)
