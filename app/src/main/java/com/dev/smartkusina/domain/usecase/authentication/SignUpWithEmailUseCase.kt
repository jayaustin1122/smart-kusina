package com.dev.smartkusina.domain.usecase.authentication

import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): AuthResult {
        return repository.signUpWithEmail(email, password, name)
    }
}
