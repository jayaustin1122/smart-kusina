package com.dev.smartkusina.data.repository

import com.dev.smartkusina.data.mapper.toDomainUser
import com.dev.smartkusina.data.mapper.toFirestoreMap
import com.dev.smartkusina.domain.model.AuthResult
import com.dev.smartkusina.domain.model.User
import com.dev.smartkusina.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val user = if (document.exists()) {
                            document.toDomainUser()
                        } else {
                            firebaseUser.toDomainUser()
                        }
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(firebaseUser.toDomainUser())
                    }
            } else {
                trySend(null)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): AuthResult {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return AuthResult.Error("User creation failed")

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Save user to Firestore
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email
            )
            saveUserToFirestore(user)

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return AuthResult.Error("Google sign in failed")

            // Save or update user in Firestore
            val user = User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )
            saveUserToFirestore(user)

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign in failed")
        }
    }

    override suspend fun signOut(): AuthResult {
        return try {
            firebaseAuth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun deleteAccount(): AuthResult {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return AuthResult.Error("No user signed in")

            // Delete user document from Firestore
            firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .delete()
                .await()

            // Delete Firebase Auth account
            currentUser.delete().await()

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Account deletion failed")
        }
    }

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(user.toFirestoreMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}