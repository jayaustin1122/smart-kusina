package com.dev.smartkusina.di

import com.dev.smartkusina.domain.repository.FavoritesRepository
import com.dev.smartkusina.domain.repository.UserRepository
import com.dev.smartkusina.domain.usecase.AddFavoriteUseCase
import com.dev.smartkusina.domain.usecase.GetCurrentUserUseCase
import com.dev.smartkusina.domain.usecase.GetFavoritesUseCase
import com.dev.smartkusina.domain.usecase.IsFavoriteUseCase
import com.dev.smartkusina.domain.usecase.LoginUserUseCase
import com.dev.smartkusina.domain.usecase.LogoutUserUseCase
import com.dev.smartkusina.domain.usecase.RegisterUserUseCase
import com.dev.smartkusina.domain.usecase.RemoveFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRegisterUserUseCase(repository: UserRepository): RegisterUserUseCase {
        return RegisterUserUseCase(repository)
    }

    @Provides
    fun provideLoginUserUseCase(repository: UserRepository): LoginUserUseCase {
        return LoginUserUseCase(repository)
    }

    @Provides
    fun provideGetCurrentUserUseCase(repository: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    fun provideLogoutUserUseCase(repository: UserRepository): LogoutUserUseCase {
        return LogoutUserUseCase(repository)
    }

    @Provides
    fun provideAddFavoriteUseCase(repository: FavoritesRepository): AddFavoriteUseCase {
        return AddFavoriteUseCase(repository)
    }
    @Provides
    fun provideRemoveFavoriteUseCase(repository: FavoritesRepository): RemoveFavoriteUseCase {
        return RemoveFavoriteUseCase(repository)
    }
    @Provides
    fun provideIsFavoriteUseCase(repository: FavoritesRepository): IsFavoriteUseCase {
        return IsFavoriteUseCase(repository)
    }
    @Provides
    fun provideGetFavoritesUseCase(repository: FavoritesRepository): GetFavoritesUseCase {
        return GetFavoritesUseCase(repository)
    }
}