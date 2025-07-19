package com.dev.smartkusina.domain.repository

import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signUpWithEmail(email: String, password: String, name: String): AuthResult
    suspend fun signInWithGoogle(account: GoogleSignInAccount): AuthResult
    suspend fun signOut(): AuthResult
    suspend fun deleteAccount(): AuthResult
    fun isUserSignedIn(): Boolean
}