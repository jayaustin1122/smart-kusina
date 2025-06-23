package com.dev.smartkusina.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.domain.usecase.LoginUserUseCase
import com.dev.smartkusina.domain.usecase.RegisterUserUseCase
import com.dev.smartkusina.domain.usecase.GetCurrentUserUseCase
import com.dev.smartkusina.domain.usecase.LogoutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class AuthAction {
    object LoginSuccess : AuthAction()
    object RegisterSuccess : AuthAction()
    object LogoutSuccess : AuthAction()
    data class Error(val message: String) : AuthAction()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUserUseCase,
    private val registerUseCase: RegisterUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
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

    fun login(name: String, password: String) {
        if (name.isBlank() || password.isBlank()) {
            _authAction.value = AuthAction.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                loginUseCase(name, password).collect { user ->
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                        _authAction.value = AuthAction.LoginSuccess
                    } else {
                        _authAction.value = AuthAction.Error("Invalid credentials")
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, password: String) {
        if (name.isBlank() || password.isBlank()) {
            _authAction.value = AuthAction.Error("Please fill in all fields")
            return
        }

        if (password.length < 6) {
            _authAction.value = AuthAction.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = UserEntity(name = name, password = password)
                registerUseCase(user).collect { isSuccess ->
                    if (isSuccess) {
                        _authAction.value = AuthAction.RegisterSuccess
                    } else {
                        _authAction.value = AuthAction.Error("Registration failed. Name might already exist.")
                    }
                }
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                logoutUserUseCase()
                _authState.value = AuthState.Unauthenticated
                _authAction.value = AuthAction.LogoutSuccess
            } catch (e: Exception) {
                _authAction.value = AuthAction.Error("Logout failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearAuthAction() {
        _authAction.value = null
    }
}