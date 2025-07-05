package com.dev.smartkusina.data.remote.mapper

import com.dev.smartkusina.data.remote.dto.MealDetailsDto
import com.dev.smartkusina.data.remote.dto.MealDto
import com.dev.smartkusina.domain.model.Ingredient
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.domain.model.MealDetails


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

// Extension function to convert MealDetailsDto to MealDetails
fun MealDetailsDto.toDomain(): MealDetails {
    val ingredients = (1..20).mapNotNull { index ->
        val name = this::class.members.find { it.name == "strIngredient$index" }
            ?.call(this) as? String
        val measure = this::class.members.find { it.name == "strMeasure$index" }
            ?.call(this) as? String

        if (!name.isNullOrBlank() && !measure.isNullOrBlank()) {
            Ingredient(name.trim(), measure.trim())
        } else null
    }

    return MealDetails(
        id = idMeal,
        name = strMeal,
        category = strCategory,
        area = strArea,
        instructions = strInstructions,
        imageUrl = strMealThumb,
        tags = strTags?.split(",")?.map { it.trim() } ?: emptyList(),
        youtubeUrl = strYoutube,
        ingredients = ingredients,
        sourceUrl = strSource
    )
}
