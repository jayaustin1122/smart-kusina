package com.dev.smartkusina.domain.usecase.authentication

import com.dev.smartkusina.domain.model.User
import com.dev.smartkusina.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}
