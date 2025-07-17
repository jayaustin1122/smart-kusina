package com.dev.smartkusina.presentation.Recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dev.smartkusina.composables.ErrorCard
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.presentation.ingredients.IngredientsViewModel
import com.dev.smartkusina.util.Response

@Composable
fun RecipesContent(
    mealsState: Response<List<Meal>>,
    favoriteIds: Set<String>,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    val ingredientsViewModel: IngredientsViewModel = hiltViewModel()
    val ingredientsUiState by ingredientsViewModel.uiState.collectAsState()
    val userIngredients = ingredientsUiState.ingredients
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        ingredientsViewModel.refreshIngredients()
    }

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

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search recipes...") },
                singleLine = true
            )
        }

        if (userIngredients.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFF28C20),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recipes sorted by ingredient matches (${userIngredients.size} ingredients)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFF28C20)
                    )
                }
            }
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
                val filteredAndSortedMeals = getFilteredAndSortedMeals(
                    meals = meals,
                    searchQuery = searchQuery,
                    userIngredients = userIngredients
                )

                if (filteredAndSortedMeals.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank())
                                    "No recipes found for '$searchQuery'"
                                else "No recipes available",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (searchQuery.isNotBlank()) {
                                Text(
                                    text = "Try searching for something else",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = filteredAndSortedMeals,
                        key = { it.idMeal }
                    ) { meal ->
                        MealCard(
                            meal = meal,
                            onClick = { onRecipeClick(meal.idMeal) },
                            isFavorite = favoriteIds.contains(meal.idMeal),
                            onFavoriteClick = { onFavoriteClick(meal.idMeal) },
                            userIngredients = userIngredients
                        )
                    }
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

private fun getFilteredAndSortedMeals(
    meals: List<Meal>,
    searchQuery: String,
    userIngredients: List<String>
): List<Meal> {
    val filteredMeals = if (searchQuery.isNotBlank()) {
        meals.filter { meal ->
            meal.strMeal.contains(searchQuery, ignoreCase = true) ||
                    meal.ingredients.any { ingredient ->
                        ingredient?.contains(searchQuery, ignoreCase = true) == true
                    }
        }
    } else {
        meals
    }

    return if (userIngredients.isNotEmpty()) {
        filteredMeals.sortedWith(compareByDescending<Meal> { recipe ->
            getIngredientMatchCount(recipe, userIngredients)
        }.thenBy { recipe ->
            recipe.strMeal
        })
    } else {
        filteredMeals
    }
}

private fun getIngredientMatchCount(meal: Meal, userIngredients: List<String>): Int {
    return meal.ingredients.count { ingredient ->
        ingredient?.let { ing ->
            userIngredients.any { userIngredient ->
                ing.contains(userIngredient, ignoreCase = true) ||
                        userIngredient.contains(ing, ignoreCase = true)
            }
        } ?: false
    }
}

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    userIngredients: List<String> = emptyList()
) {
    val matchCount = getIngredientMatchCount(meal, userIngredients)
    val totalIngredients = meal.ingredients.count { !it.isNullOrBlank() }
    val matchPercentage = if (totalIngredients > 0) {
        (matchCount.toFloat() / totalIngredients * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (matchCount > 0) Color(0xFFFFF8F0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.strMeal,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (userIngredients.isNotEmpty()) {
                    if (matchCount > 0) {
                        Text(
                            text = "✓ $matchCount/$totalIngredients ingredients ($matchPercentage%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF28C20),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    } else {
                        Text(
                            text = "No matching ingredients",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                meal.strCategory?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF28C20)
                    )
                }

                meal.strArea?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            onFavoriteClick?.let { callback ->
                IconButton(
                    onClick = { callback() }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}