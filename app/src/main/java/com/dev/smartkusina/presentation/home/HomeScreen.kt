package com.dev.smartkusina.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.smartkusina.domain.model.Meal
import com.dev.smartkusina.presentation.auth.LoginViewModel
import com.dev.smartkusina.presentation.auth.state.AuthAction
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.util.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val authAction by viewModel.authAction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mealsState by homeViewModel.mealsState.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchRandomMeals()
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            when (val state = authState) {
//                is AuthState.Authenticated -> {
//                    Text(
//                        text = "Welcome, ${state.user.name}!",
//                        style = MaterialTheme.typography.headlineMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFFF28C20)
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Text(
//                        text = "Your smart kitchen companion is ready!",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = Color(0xFF666666)
//                    )
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8F0))
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            Text(
//                                text = "🍳 Recipe Suggestions",
//                                style = MaterialTheme.typography.titleMedium,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = "Discover new recipes based on your preferences",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        }
//                    }
//                }
//                is AuthState.Loading -> {
//                    CircularProgressIndicator(color = Color(0xFFF28C20))
//                }
//                else -> {
//                }
//            }
            when (mealsState) {
                is Response.Loading -> {
                    CircularProgressIndicator()
                }
                is Response.Success -> {
                    val meals = (mealsState as Response.Success<List<Meal>>).data
                    LazyColumn {
                        items(meals.orEmpty()) { meal ->
                            Text(text = meal.strMeal)
                        }
                    }
                }
                is Response.Error -> {
                    Text("Error: ${(mealsState as Response.Error).message}")
                }
            }
        }
    }
}