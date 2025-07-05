package com.dev.smartkusina.di

import android.content.Context
import androidx.room.Room
import com.dev.smartkusina.data.local.dao.FavoritesDao
import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.local.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): Database {
        return Room.databaseBuilder(
            appContext,
            Database::class.java,
            Database.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: Database): UserDao = database.userDao()

    @Provides
    fun provideFavoritesDao(database: Database): FavoritesDao = database.favoritesDao()
}