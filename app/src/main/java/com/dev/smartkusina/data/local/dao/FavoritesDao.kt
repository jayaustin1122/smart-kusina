package com.dev.smartkusina.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.smartkusina.data.local.entity.FavoriteEntity

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteRecipe(favorite: FavoriteEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE recipeId = :recipeId)")
    suspend fun isFavorite(recipeId: String): Boolean

    @Query("DELETE FROM favorites WHERE recipeId = :recipeId")
    suspend fun removeFavoriteRecipe(recipeId: String)

    @Query("SELECT recipeId FROM favorites")
    suspend fun getAllFavoriteIds(): List<String>
}