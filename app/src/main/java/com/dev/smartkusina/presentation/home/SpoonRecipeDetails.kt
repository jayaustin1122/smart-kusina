package com.dev.smartkusina.presentation.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dev.smartkusina.domain.model.SpoonRecipe

@Composable
fun SpoonRecipeDetails(
    mealId: String,
    onNavigateBack: () -> Unit,
    recipe : SpoonRecipe
) {
    Text(text = recipe.title)
}