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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.dev.smartkusina.util.Response
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun HomeContent(
    authState: AuthState,
    spoonRecipeState: Response<List<SpoonRecipe>>,
    similarState: Response<List<SimilarRecipe>>,
    onRecipeClick: (String) -> Unit,
//    favoriteIds: Set<String>
//    onFavoriteClick: (String) -> Unit
) {
    val isRefreshing = spoonRecipeState is Response.Loading
    val homeViewModel: HomeViewModel = hiltViewModel()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { homeViewModel.fetchRandomMeals() }
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
                        CircularProgressIndicator(color = Color(0xFFF28C20))
                    }

                    else -> {}
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
                    items(meals) { meal ->
                        MealCardSpoon(
                            meal = meal,
                            onClick = {
                                homeViewModel.setRecipe(meal)
                                onRecipeClick(meal.id.toString())  }
                        )
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



@Composable
fun MealCardSpoon(
    meal: SpoonRecipe,
    onClick: () -> Unit,
//    isFavorite: Boolean = false,
//    onFavoriteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = meal.image,
                contentDescription = meal.title,
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
                    text = meal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}