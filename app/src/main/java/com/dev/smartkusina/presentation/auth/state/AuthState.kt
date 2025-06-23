package com.dev.smartkusina.presentation.auth.state

import com.dev.smartkusina.data.local.entity.UserEntity

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