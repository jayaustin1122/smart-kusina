package com.dev.smartkusina.data.repository

import android.util.Log
import com.dev.smartkusina.data.local.dao.FavoritesDao
import com.dev.smartkusina.data.local.entity.FavoriteEntity
import com.dev.smartkusina.domain.repository.FavoritesRepository
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao
) : FavoritesRepository {

    override suspend fun addFavorite(recipeId: String) {
        favoritesDao.addFavoriteRecipe(FavoriteEntity(recipeId = recipeId))
        Log.d("FavoritesRepository", "addFavorite: Recipe $recipeId added to favorites")
    }

    override suspend fun removeFavorite(recipeId: String) {
        favoritesDao.removeFavoriteRecipe(recipeId)
    }

    override suspend fun isFavorite(recipeId: String): Boolean {
        return favoritesDao.isFavorite(recipeId)
    }

    override suspend fun getAllFavorites(): List<String> {
        return favoritesDao.getAllFavoriteIds()
    }
}