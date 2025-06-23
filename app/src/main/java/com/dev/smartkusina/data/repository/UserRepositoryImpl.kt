package com.dev.smartkusina.data.repository

import android.util.Log
import com.dev.smartkusina.data.local.dao.UserDao
import com.dev.smartkusina.data.local.entity.UserEntity
import com.dev.smartkusina.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun loginUser(name: String, password: String): Flow<UserEntity?> = flow {
        try {
            val user = userDao.signInUser(name, password)
            if (user != null) {
                // Set user as logged in
                userDao.setUserLoggedIn(user.userId)
                // userDao.createUserSession(user.userId, System.currentTimeMillis())
            }
            emit(user)
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun registerUser(user: UserEntity): Flow<Boolean> = flow {
        try {
            // Check if name already exists
            val nameExists = userDao.checkNameExists(user.name) > 0
            if (nameExists) {
                emit(false)
                return@flow
            }

            // Register the user (ID will be auto-generated)
            userDao.signUpUser(user)
            emit(true)
            Log.d("UserRepositoryImpl", "registerUser: User registered successfully with name: ${user.name}")
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "registerUser: Error registering user", e)
            emit(false)
        }
    }

    override suspend fun getCurrentUser(): Flow<UserEntity?> {
        return userDao.getCurrentUser()
        // return userDao.getCurrentUserFromSession()
    }

    override suspend fun logout() {
        try {
            userDao.logoutUser()
            // userDao.clearAllSessions()
        } catch (e: Exception) {
            // Handle logout error
        }
    }
}