package com.dev.smartkusina.domain.repository

interface FavoritesRepository {
    suspend fun addFavorite(recipeId: String)
    suspend fun removeFavorite(recipeId: String)
    suspend fun isFavorite(recipeId: String): Boolean
    suspend fun getAllFavorites(): List<String>
}