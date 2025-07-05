package com.dev.smartkusina.data.remote.mapper

import com.dev.smartkusina.data.remote.dto.SimilarRecipeDto
import com.dev.smartkusina.domain.model.SimilarRecipe

fun SimilarRecipeDto.toDomain() = SimilarRecipe(
    id = id,
    image = image,
    imageType = imageType,
    title = title,
    readyInMinutes = readyInMinutes,
    servings = servings,
    sourceUrl = sourceUrl
)