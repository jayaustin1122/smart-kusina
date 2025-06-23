package com.dev.smartkusina.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.smartkusina.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun signUpUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE name = :name AND password = :password")
    suspend fun signInUser(name: String, password: String): UserEntity?

    // Get current logged-in user
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    // Set user as logged in
    @Query("UPDATE users SET isLoggedIn = 1 WHERE userId = :userId")
    suspend fun setUserLoggedIn(userId: Int)

    // Logout user (set all users as logged out)
    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutUser()

    // Check if name already exists
    @Query("SELECT COUNT(*) FROM users WHERE name = :name")
    suspend fun checkNameExists(name: String): Int

    @Query("SELECT u.* FROM users u INNER JOIN user_sessions s ON u.userId = s.userId WHERE s.isActive = 1 LIMIT 1")
    fun getCurrentUserFromSession(): Flow<UserEntity?>

    @Query("INSERT OR REPLACE INTO user_sessions (userId, isActive, loginTimestamp) VALUES (:userId, 1, :timestamp)")
    suspend fun createUserSession(userId: Int, timestamp: Long)

    @Query("UPDATE user_sessions SET isActive = 0")
    suspend fun clearAllSessions()
}