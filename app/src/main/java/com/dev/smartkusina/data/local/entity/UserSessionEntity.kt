package com.dev.smartkusina.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey
    val userId: String,
    val isActive: Boolean = true,
    val loginTimestamp: Long = System.currentTimeMillis()
)