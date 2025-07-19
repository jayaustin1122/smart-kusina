package com.dev.smartkusina.domain.usecase.authentication

import com.dev.smartkusina.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserSignedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean = repository.isUserSignedIn()
}