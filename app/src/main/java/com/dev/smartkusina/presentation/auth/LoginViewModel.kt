package com.dev.smartkusina.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.usecase.authentication.GetCurrentUserUseCase
import com.dev.smartkusina.domain.usecase.authentication.SignInWithEmailUseCase
import com.dev.smartkusina.domain.usecase.authentication.SignInWithGoogleUseCase
import com.dev.smartkusina.domain.usecase.authentication.SignOutUseCase
import com.dev.smartkusina.domain.usecase.authentication.SignUpWithEmailUseCase
import com.dev.smartkusina.presentation.auth.state.AuthAction
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authAction = MutableStateFlow<AuthAction?>(null)
    val authAction: StateFlow<AuthAction?> = _authAction.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                getCurrentUserUseCase().collect { user ->
                    _authState.value = if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authAction.value = AuthAction.Error("Please fill in all fields")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authAction.value = AuthAction.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                when (val result = signInWithEmailUseCase(email, password)) {
                    is AuthResult.Success -> {
                        _authAction.value = AuthAction.LoginSuccess
                    }
                    is AuthResult.Error -> {
                        _authAction.value = AuthAction.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Sign in failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authAction.value = AuthAction.Error("Please fill in all fields")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authAction.value = AuthAction.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _authAction.value = AuthAction.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                when (val result = signUpWithEmailUseCase(email, password, name)) {
                    is AuthResult.Success -> {
                        _authAction.value = AuthAction.RegisterSuccess
                    }
                    is AuthResult.Error -> {
                        _authAction.value = AuthAction.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Sign up failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                when (val result = signInWithGoogleUseCase(account)) {
                    is AuthResult.Success -> {
                        _authAction.value = AuthAction.GoogleSignInSuccess
                    }
                    is AuthResult.Error -> {
                        _authAction.value = AuthAction.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Google sign in failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                when (val result = signOutUseCase()) {
                    is AuthResult.Success -> {
                        _authState.value = AuthState.Unauthenticated
                        _authAction.value = AuthAction.LogoutSuccess
                    }
                    is AuthResult.Error -> {
                        _authAction.value = AuthAction.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Sign out failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearAuthAction() {
        _authAction.value = null
    }
}