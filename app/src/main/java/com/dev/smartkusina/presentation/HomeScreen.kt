package com.dev.smartkusina.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.smartkusina.presentation.Recipes.RecipesContent
import com.dev.smartkusina.presentation.auth.LoginViewModel
import com.dev.smartkusina.presentation.auth.state.AuthAction
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.presentation.favorites.FavoritesScreen
import com.dev.smartkusina.presentation.favorites.FavoritesViewModel
import com.dev.smartkusina.presentation.home.HomeContent
import com.dev.smartkusina.presentation.home.HomeViewModel
import com.dev.smartkusina.presentation.profile.ProfileContent
import com.dev.smartkusina.util.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    onNavigateToSpoonDetails: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {

    val authState by viewModel.authState.collectAsState()
    val authAction by viewModel.authAction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mealsState by homeViewModel.mealsState.collectAsState()
    val spoonRecipeState by homeViewModel.spoonRecipesState.collectAsState()
    val similarState by homeViewModel.similarRecipesState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val isRefreshing = mealsState is Response.Loading
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    LaunchedEffect(Unit) {
        if (homeViewModel.mealsState.value !is Response.Success) {
            homeViewModel.fetchRandomMeals()
        }
    }

    LaunchedEffect(Unit) {
        if (homeViewModel.spoonRecipesState.value !is Response.Success) {
            homeViewModel.fetchRandomSpoonRecipes()
            Log.d("HomeScreen", "HomeScreen: value : ${homeViewModel.spoonRecipesState.value}")
        }
    }
    LaunchedEffect(authAction) {
        authAction?.let { action ->
            when (action) {
                is AuthAction.LogoutSuccess -> {
                    onNavigateToLogin()
                }

                else -> {}
            }
            viewModel.clearAuthAction()
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                onNavigateToLogin()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Smart Kusina",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout()
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF28C20),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                HomeTab.HOME -> HomeContent(
                    authState = authState,
                    spoonRecipeState = spoonRecipeState,
                    similarState = similarState,
                    onRecipeClick = {
                        onNavigateToSpoonDetails(it)
                    }
                )

                HomeTab.RECIPES -> RecipesContent(
                    mealsState = mealsState,
                    onRecipeClick = onNavigateToRecipeDetail,
                    favoriteIds = favoriteIds,
                    onFavoriteClick = { recipeId ->
                        favoritesViewModel.toggleFavorite(recipeId)
                    }

                )

                HomeTab.PROFILE -> ProfileContent(authState = authState)
                HomeTab.FAVORITES -> {
                    FavoritesScreen(
                        onRecipeClick = onNavigateToRecipeDetail
                    )
                }
            }
        }
    }
}