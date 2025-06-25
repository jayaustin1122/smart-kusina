package com.dev.smartkusina.data.remote.mapper

import com.dev.smartkusina.data.remote.dto.MealDto
import com.dev.smartkusina.domain.model.Meal


fun MealDto.toMeal(): Meal {
    val ingredients = listOf(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    ).filter { !it.isNullOrBlank() }

    val measures = listOf(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    ).filter { !it.isNullOrBlank() }

    return Meal(
        idMeal = idMeal,
        strMeal = strMeal,
        strCategory = strCategory,
        strArea = strArea,
        strInstructions = strInstructions,
        strMealThumb = strMealThumb,
        strYoutube = strYoutube,
        ingredients = ingredients,
        measures = measures
    )
}
