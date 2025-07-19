package com.dev.smartkusina.domain.usecase.authentication

import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult {
        return repository.signOut()
    }
}