package com.dev.smartkusina.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    RECIPES("Recipes", Icons.Default.Star),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.Person)
}