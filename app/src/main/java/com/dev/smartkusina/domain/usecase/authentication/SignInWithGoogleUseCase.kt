package com.dev.smartkusina.domain.usecase.authentication

import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(account: GoogleSignInAccount): AuthResult {
        return repository.signInWithGoogle(account)
    }
}