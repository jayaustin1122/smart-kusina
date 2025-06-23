package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, password: String): Flow<UserEntity?> {
        return repository.loginUser(name, password)
    }
}