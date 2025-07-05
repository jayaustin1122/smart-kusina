package com.dev.smartkusina.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity (
    @PrimaryKey
    val recipeId: String = ""
)