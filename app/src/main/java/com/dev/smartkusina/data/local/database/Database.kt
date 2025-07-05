package com.dev.smartkusina.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.smartkusina.data.local.dao.FavoritesDao
import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.local.entity.FavoriteEntity
import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.data.local.entity.UserSessionEntity


@Database(
    entities = [
        UserEntity::class,
        FavoriteEntity::class,
        UserSessionEntity::class
               ],
    version = 2,
    exportSchema = false
)

abstract class Database : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "smart_kusina_database"
    }
}