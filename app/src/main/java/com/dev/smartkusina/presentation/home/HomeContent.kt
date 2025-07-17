package com.dev.smartkusina.presentation.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.dev.smartkusina.composables.WelcomeCard
import com.dev.smartkusina.domain.model.SimilarRecipe
import com.dev.smartkusina.domain.model.SpoonRecipe
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.presentation.ingredients.IngredientsViewModel
import com.dev.smartkusina.util.Response
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun HomeContent(
    authState: AuthState,
    spoonRecipeState: Response<List<SpoonRecipe>>,
    similarState: Response<List<SimilarRecipe>>,
    onRecipeClick: (String) -> Unit,
) {
    val isRefreshing = spoonRecipeState is Response.Loading
    val homeViewModel: HomeViewModel = hiltViewModel()
    val ingredientsViewModel: IngredientsViewModel = hiltViewModel()

    val ingredientsUiState by ingredientsViewModel.uiState.collectAsState()
    val userIngredients = ingredientsUiState.ingredients
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        ingredientsViewModel.refreshIngredients()
        homeViewModel.refreshData()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            homeViewModel.refreshData()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                when (authState) {
                    is AuthState.Authenticated -> {
                        WelcomeCard(userName = authState.user.name)
                    }
                    is AuthState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFF28C20))
                        }
                    }
                    else -> {}
                }
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

            item {
                Text(
                    text = "Featured Recipes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF28C20)
                )
            }

            when (spoonRecipeState) {
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
                    val meals = spoonRecipeState.data.orEmpty()
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
                            key = { it.id }
                        ) { meal ->
                            MealCardSpoon(
                                meal = meal,
                                onClick = {
                                    homeViewModel.setRecipe(meal)
                                    onRecipeClick(meal.id.toString())
                                },
                                userIngredients = userIngredients
                            )
                        }
                    }
                }

                is Response.Error -> {
                    item {
                        Log.d("Home", "HomeContent: Error: ${spoonRecipeState.message}")
                        ErrorCard(message = spoonRecipeState.message.toString())
                    }
                }
            }
        }
    }
}

private fun getFilteredAndSortedMeals(
    meals: List<SpoonRecipe>,
    searchQuery: String,
    userIngredients: List<String>
): List<SpoonRecipe> {
    val filteredMeals = if (searchQuery.isNotBlank()) {
        meals.filter { meal ->
            meal.title.contains(searchQuery, ignoreCase = true) ||
                    meal.extendedIngredients.any { ingredient ->
                        ingredient.name.contains(searchQuery, ignoreCase = true)
                    }
        }
    } else {
        meals
    }

    return if (userIngredients.isNotEmpty()) {
        filteredMeals.sortedWith(compareByDescending<SpoonRecipe> { recipe ->
            getIngredientMatchCount(recipe, userIngredients)
        }.thenBy { recipe ->
            recipe.title
        })
    } else {
        filteredMeals
    }
}

private fun getIngredientMatchCount(recipe: SpoonRecipe, userIngredients: List<String>): Int {
    return recipe.extendedIngredients.count { ingredient ->
        userIngredients.any { userIngredient ->
            ingredient.name.contains(userIngredient, ignoreCase = true) ||
                    userIngredient.contains(ingredient.name, ignoreCase = true)
        }
    }
}

@Composable
fun MealCardSpoon(
    meal: SpoonRecipe,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    userIngredients: List<String>
) {
    val matchCount = getIngredientMatchCount(meal, userIngredients)
    val matchPercentage = if (meal.extendedIngredients.isNotEmpty()) {
        (matchCount.toFloat() / meal.extendedIngredients.size * 100).toInt()
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = meal.image,
                    contentDescription = meal.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (matchCount > 0) {
                        Text(
                            text = "✓ $matchCount/${meal.extendedIngredients.size} ingredients ($matchPercentage%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF28C20),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (userIngredients.isNotEmpty()) {
                        Text(
                            text = "No matching ingredients",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = "${meal.extendedIngredients.size} ingredients • ${meal.readyInMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                onFavoriteClick?.let { callback ->
                    IconButton(onClick = { callback() }) {
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
}