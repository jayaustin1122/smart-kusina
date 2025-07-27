package com.dev.smartkusina.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.dev.smartkusina.domain.model.MealDetails
import com.dev.smartkusina.util.Response

@Composable
fun FavoritesScreen(
    onRecipeClick: (String) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favoriteMeals by viewModel.favoriteMeals.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF28C20),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (favoriteMeals) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFF28C20))
                }
            }

            is Response.Success -> {
                val meals = favoriteMeals.data.orEmpty()
                if (meals.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFF28C20)
                            )
                            Text(
                                text = "No Favorites Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Start adding recipes to your favorites!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(meals) { meal ->
                            FavoriteMealCard(
                                meal = meal,
                                onClick = { onRecipeClick(meal.id) },
                                onRemoveFromFavorites = {
                                    viewModel.toggleFavorite(meal.id)
                                }
                            )
                        }
                    }
                }
            }

            is Response.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Error loading favorites",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = favoriteMeals.message ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { viewModel.loadFavorites() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF28C20)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteMealCard(
    meal: MealDetails,
    onClick: () -> Unit,
    onRemoveFromFavorites: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.imageUrl ?: "",
                contentDescription = meal.name ?: "Meal image",
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
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                meal.category?.let { category ->
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF28C20)
                    )
                }

                meal.area?.let { area ->
                    Text(
                        text = area,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorited",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = Color.Gray
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Remove from Favorites")
            },
            text = {
                Text("Are you sure you want to remove \"${meal.name}\" from your favorites?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveFromFavorites()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Remove", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}