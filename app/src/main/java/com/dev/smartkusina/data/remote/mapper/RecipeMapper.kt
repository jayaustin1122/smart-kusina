package com.dev.smartkusina.data.remote.mapper

import com.dev.smartkusina.data.remote.dto.*
import com.dev.smartkusina.domain.model.*

fun RecipeDto.toDomain(): Recipes {
    return Recipes(
        id = id,
        name = name ?: "Unknown",
        ingredients = ingredients ?: emptyList(),
        instructions = instructions,
        prepTimeMinutes = prepTimeMinutes ?: 0,
        cookTimeMinutes = cookTimeMinutes ?: 0,
        servings = servings ?: 1,
        difficulty = difficulty ?: "Unknown",
        cuisine = cuisine ?: "Various",
        caloriesPerServing = caloriesPerServing ?: 0,
        tags = tags ?: emptyList(),
        userId = userId,
        image = image ?: "",
        rating = rating ?: 0.0,
        reviewCount = reviewCount ?: 0,
        mealType = mealType ?: emptyList()
    )
}

fun SpoonRecipeDto.toDomain(): SpoonRecipe {
    return SpoonRecipe(
        id = id,
        image = image ?: "",
        imageType = imageType ?: "jpg",
        title = title ?: "Untitled Recipe",
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl ?: "",
        vegetarian = vegetarian,
        vegan = vegan,
        glutenFree = glutenFree,
        dairyFree = dairyFree,
        veryHealthy = veryHealthy,
        cheap = cheap,
        veryPopular = veryPopular,
        sustainable = sustainable,
        lowFodmap = lowFodmap,
        weightWatcherSmartPoints = weightWatcherSmartPoints,
        gaps = gaps ?: "",
        preparationMinutes = preparationMinutes,
        cookingMinutes = cookingMinutes,
        aggregateLikes = aggregateLikes,
        healthScore = healthScore,
        creditsText = creditsText ?: "Unknown",
        license = license ?: "N/A",
        sourceName = sourceName ?: "Unknown",
        pricePerServing = pricePerServing,
        extendedIngredients = extendedIngredients?.map { it.toDomain() } ?: emptyList(),
        summary = summary ?: "No summary available",
        cuisines = cuisines ?: emptyList(),
        dishTypes = dishTypes ?: emptyList(),
        diets = diets ?: emptyList(),
        occasions = occasions ?: emptyList(),
        instructions = instructions ?: "Instructions not available",
        analyzedInstructions = analyzedInstructions?.map { it.toDomain() } ?: emptyList(),
        originalId = originalId,
        spoonacularScore = spoonacularScore,
        spoonacularSourceUrl = spoonacularSourceUrl ?: ""
    )
}

fun ExtendedIngredientDto.toDomain(): ExtendedIngredient {
    return ExtendedIngredient(
        id = id,
        aisle = aisle ?: "",
        image = image ?: "",
        consistency = consistency ?: "solid",
        name = name ?: "Unknown",
        nameClean = nameClean ?: name,
        original = original ?: "",
        originalName = originalName ?: name,
        amount = amount,
        unit = unit ?: "",
        meta = meta ?: emptyList(),
        measures = measures.toDomain()
    )
}

fun MeasuresDto.toDomain(): Measures {
    return Measures(
        us = us?.toDomain() ?: Us(0.0, "", ""),
        metric = metric?.toDomain() ?: Metric(0.0, "", "")
    )
}

fun UsDto.toDomain(): Us {
    return Us(
        amount = amount,
        unitShort = unitShort ?: "",
        unitLong = unitLong ?: ""
    )
}

fun MetricDto.toDomain(): Metric {
    return Metric(
        amount = amount,
        unitShort = unitShort ?: "",
        unitLong = unitLong ?: ""
    )
}

fun AnalyzedInstructionDto.toDomain(): AnalyzedInstruction {
    return AnalyzedInstruction(
        name = name ?: "",
        steps = steps?.map { it.toDomain() } ?: emptyList()
    )
}

fun StepDto.toDomain(): Step {
    return Step(
        number = number,
        step = step ?: "Step not available",
        ingredients = ingredients?.map { it.toDomain() } ?: emptyList(),
        equipment = equipment?.map { it.toDomain() } ?: emptyList(),
        length = length?.toDomain()
    )
}

fun IngredientDto.toDomain(): IngredientRecipe {
    return IngredientRecipe(
        id = id,
        name = name ?: "Unknown",
        localizedName = localizedName ?: name ?: "Unknown",
        image = image ?: ""
    )
}

fun EquipmentDto.toDomain(): Equipment {
    return Equipment(
        id = id,
        name = name ?: "Unknown",
        localizedName = localizedName ?: name ?: "Unknown",
        image = image ?: ""
    )
}

fun LengthDto.toDomain(): Length {
    return Length(
        number = number,
        unit = unit ?: ""
    )
}
