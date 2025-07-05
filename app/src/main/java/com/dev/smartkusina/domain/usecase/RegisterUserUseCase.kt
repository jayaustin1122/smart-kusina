package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(user: UserEntity): Flow<Boolean> {
        return repository.registerUser(user)
    }
}