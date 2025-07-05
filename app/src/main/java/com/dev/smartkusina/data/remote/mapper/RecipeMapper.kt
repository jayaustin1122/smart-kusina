package com.dev.smartkusina.data.remote.mapper

import com.dev.smartkusina.data.remote.dto.AnalyzedInstructionDto
import com.dev.smartkusina.data.remote.dto.EquipmentDto
import com.dev.smartkusina.data.remote.dto.ExtendedIngredientDto
import com.dev.smartkusina.data.remote.dto.IngredientDto
import com.dev.smartkusina.data.remote.dto.LengthDto
import com.dev.smartkusina.data.remote.dto.MeasuresDto
import com.dev.smartkusina.data.remote.dto.MetricDto
import com.dev.smartkusina.data.remote.dto.RecipeDto
import com.dev.smartkusina.data.remote.dto.SpoonRecipeDto
import com.dev.smartkusina.data.remote.dto.StepDto
import com.dev.smartkusina.data.remote.dto.UsDto
import com.dev.smartkusina.domain.model.AnalyzedInstruction
import com.dev.smartkusina.domain.model.Equipment
import com.dev.smartkusina.domain.model.ExtendedIngredient
import com.dev.smartkusina.domain.model.IngredientRecipe
import com.dev.smartkusina.domain.model.Length
import com.dev.smartkusina.domain.model.Measures
import com.dev.smartkusina.domain.model.Metric
import com.dev.smartkusina.domain.model.Recipes
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.domain.model.Step
import com.dev.smartkusina.domain.model.Us

fun RecipeDto.toDomain(): Recipes {
    return Recipes(
        id = id,
        name = name,
        ingredients = ingredients,
        instructions = instructions,
        prepTimeMinutes = prepTimeMinutes,
        cookTimeMinutes = cookTimeMinutes,
        servings = servings,
        difficulty = difficulty,
        cuisine = cuisine,
        caloriesPerServing = caloriesPerServing,
        tags = tags,
        userId = userId,
        image = image,
        rating = rating,
        reviewCount = reviewCount,
        mealType = mealType
    )
}

fun SpoonRecipeDto.toDomain(): SpoonRecipe {
    return SpoonRecipe(
        id = id,
        image = image,
        imageType = imageType,
        title = title,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
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
        gaps = gaps,
        preparationMinutes = preparationMinutes,
        cookingMinutes = cookingMinutes,
        aggregateLikes = aggregateLikes,
        healthScore = healthScore,
        creditsText = creditsText,
        license = license,
        sourceName = sourceName,
        pricePerServing = pricePerServing,
        extendedIngredients = extendedIngredients.map { it.toDomain() },
        summary = summary,
        cuisines = cuisines,
        dishTypes = dishTypes,
        diets = diets,
        occasions = occasions,
        instructions = instructions,
        analyzedInstructions = analyzedInstructions.map { it.toDomain() },
        originalId = originalId,
        spoonacularScore = spoonacularScore,
        spoonacularSourceUrl = spoonacularSourceUrl
    )
}

fun ExtendedIngredientDto.toDomain(): ExtendedIngredient {
    return ExtendedIngredient(
        id = id,
        aisle = aisle,
        image = image,
        consistency = consistency,
        name = name,
        nameClean = nameClean,
        original = original,
        originalName = originalName,
        amount = amount,
        unit = unit,
        meta = meta,
        measures = measures.toDomain()
    )
}

fun MeasuresDto.toDomain(): Measures {
    return Measures(
        us = us.toDomain(),
        metric = metric.toDomain()
    )
}

fun UsDto.toDomain(): Us {
    return Us(
        amount = amount,
        unitShort = unitShort,
        unitLong = unitLong
    )
}

fun MetricDto.toDomain(): Metric {
    return Metric(
        amount = amount,
        unitShort = unitShort,
        unitLong = unitLong
    )
}

fun AnalyzedInstructionDto.toDomain(): AnalyzedInstruction {
    return AnalyzedInstruction(
        name = name,
        steps = steps.map { it.toDomain() }
    )
}

fun StepDto.toDomain(): Step {
    return Step(
        number = number,
        step = step,
        ingredients = ingredients.map { it.toDomain() },
        equipment = equipment.map { it.toDomain() },
        length = length?.toDomain()
    )
}

fun IngredientDto.toDomain(): IngredientRecipe {
    return IngredientRecipe(
        id = id,
        name = name,
        localizedName = localizedName,
        image = image
    )
}

fun EquipmentDto.toDomain(): Equipment {
    return Equipment(
        id = id,
        name = name,
        localizedName = localizedName,
        image = image
    )
}

fun LengthDto.toDomain(): Length {
    return Length(
        number = number,
        unit = unit
    )
}