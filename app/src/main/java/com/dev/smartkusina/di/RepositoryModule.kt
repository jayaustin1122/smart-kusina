package com.dev.smartkusina.di

import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.repository.UserRepositoryImpl
import com.dev.smartkusina.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }
}