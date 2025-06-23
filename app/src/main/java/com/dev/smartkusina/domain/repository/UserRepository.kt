package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun loginUser(name: String, password: String): Flow<UserEntity?>
    suspend fun registerUser(user: UserEntity): Flow<Boolean>
    suspend fun getCurrentUser(): Flow<UserEntity?>
    suspend fun logout()
}