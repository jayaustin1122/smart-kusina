package com.dev.smartkusina.presentation.Recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.presentation.home.ErrorCard
import com.dev.smartkusina.presentation.home.MealCard
import com.dev.smartkusina.util.Response

@Composable
fun RecipesContent(
    mealsState: Response<List<Meal>>,
    onRecipeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "All Recipes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )
        }

        when (mealsState) {
            is Response.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFF28C20))
                    }
                }
            }
            is Response.Success -> {
                val meals = mealsState.data.orEmpty()
                items(meals) { meal ->
                    MealCard(
                        meal = meal,
                        onClick = { onRecipeClick(meal.idMeal) }
                    )
                }
            }
            is Response.Error -> {
                item {
                    ErrorCard(message = mealsState.message.toString())
                }
            }
        }
    }
}