package com.dev.smartkusina.presentation.auth.state

import com.dev.smartkusina.domain.model.User

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class AuthAction {
    object LoginSuccess : AuthAction()
    object RegisterSuccess : AuthAction()
    object GoogleSignInSuccess : AuthAction()
    object LogoutSuccess : AuthAction()
    data class Error(val message: String) : AuthAction()
}