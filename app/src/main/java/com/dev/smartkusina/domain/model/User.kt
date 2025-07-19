package com.dev.smartkusina.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)