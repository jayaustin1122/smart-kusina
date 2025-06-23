package com.dev.smartkusina.domain.usecase

import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<UserEntity?> {
        return userRepository.getCurrentUser()
    }
}