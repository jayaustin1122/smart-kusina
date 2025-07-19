package com.dev.smartkusina.presentation.auth.state

import com.dev.smartkusina.domain.model.User

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class AuthAction {
    data object LoginSuccess : AuthAction()
    data object RegisterSuccess : AuthAction()
    data object GoogleSignInSuccess : AuthAction()
    data object LogoutSuccess : AuthAction()
    data class Error(val message: String) : AuthAction()
}