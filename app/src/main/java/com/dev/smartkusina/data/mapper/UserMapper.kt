package com.dev.smartkusina.data.mapper

import com.dev.smartkusina.domain.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

fun FirebaseUser.toDomainUser(): User {
    return User(
        uid = uid,
        name = displayName ?: "",
        email = email ?: "",
        photoUrl = photoUrl?.toString()
    )
}

fun DocumentSnapshot.toDomainUser(): User? {
    return try {
        User(
            uid = getString("uid") ?: return null,
            name = getString("name") ?: "",
            email = getString("email") ?: "",
            photoUrl = getString("photoUrl"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

fun User.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "photoUrl" to (photoUrl ?: ""),
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}