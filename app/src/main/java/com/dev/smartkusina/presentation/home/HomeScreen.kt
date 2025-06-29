package com.dev.smartkusina.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.presentation.BottomNavigation
import com.dev.smartkusina.presentation.Recipes.RecipesContent
import com.dev.smartkusina.presentation.auth.LoginViewModel
import com.dev.smartkusina.presentation.auth.state.AuthAction
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.presentation.favorites.FavoritesScreen
import com.dev.smartkusina.presentation.profile.ProfileContent
import com.dev.smartkusina.util.HomeTab
import com.dev.smartkusina.util.Response
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val authState by viewModel.authState.collectAsState()
    val authAction by viewModel.authAction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mealsState by homeViewModel.mealsState.collectAsState()
    val isRefreshing = mealsState is Response.Loading
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    LaunchedEffect(Unit) {
        if (homeViewModel.mealsState.value !is Response.Success) {
            homeViewModel.fetchRandomMeals()
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
                    mealsState = mealsState,
                    onRecipeClick = onNavigateToRecipeDetail
                )
                HomeTab.RECIPES -> RecipesContent(
                    mealsState = mealsState,
                    onRecipeClick = onNavigateToRecipeDetail
                )
                HomeTab.FAVORITES -> FavoritesScreen()
                HomeTab.PROFILE -> ProfileContent(authState = authState)
            }
        }
    }
}

@Composable
fun HomeContent(
    authState: AuthState,
    mealsState: Response<List<Meal>>,
    onRecipeClick: (String) -> Unit
) {
    val isRefreshing = mealsState is Response.Loading
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
                    val meals = mealsState.data.orEmpty().take(5)
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
}

@Composable
fun WelcomeCard(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8F0))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Welcome, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF28C20)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your smart kitchen companion is ready!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit
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
        }
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFFF28C20)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}