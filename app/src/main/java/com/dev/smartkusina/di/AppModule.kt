package com.dev.smartkusina.di

import android.content.Context
import androidx.room.Room
import com.dev.smartkusina.data.local.dao.FavoritesDao
import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.local.database.Database
import com.dev.smartkusina.data.repository.UserRepositoryImpl
import com.dev.smartkusina.domain.repository.UserRepository
import com.dev.smartkusina.domain.usecase.GetCurrentUserUseCase
import com.dev.smartkusina.domain.usecase.LoginUserUseCase
import com.dev.smartkusina.domain.usecase.LogoutUserUseCase
import com.dev.smartkusina.domain.usecase.RegisterUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideFavoritesDao(database: Database): FavoritesDao {
        return database.favoritesDao()
    }
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }

    @Provides
    fun provideRegisterUserUseCase(repo: UserRepository): RegisterUserUseCase {
        return RegisterUserUseCase(repo)
    }

    @Provides
    fun provideLoginUserUseCase(repo: UserRepository): LoginUserUseCase {
        return LoginUserUseCase(repo)
    }

    @Provides
    fun provideGetCurrentUserUseCase(repo: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repo)
    }

    @Provides
    fun provideLogoutUserUseCase(repo: UserRepository): LogoutUserUseCase {
        return LogoutUserUseCase(repo)
    }
}