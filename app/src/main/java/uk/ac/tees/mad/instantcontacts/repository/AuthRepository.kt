package uk.ac.tees.mad.instantcontacts.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.instantcontacts.domain.AuthState

class AuthRepository {
    private val auth = Firebase.auth

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<AuthState<FirebaseUser>> = callbackFlow {
        trySend(AuthState.Loading)
        try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            trySend(AuthState.Success(user!!))
        } catch (e: Exception) {
            trySend(AuthState.Error(e))
        }
        close()
    }

    fun login(
        email: String,
        password: String
    ): Flow<AuthState<FirebaseUser>> = callbackFlow {
        trySend(AuthState.Loading)
        try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            trySend(AuthState.Success(user!!))
        } catch (e: Exception) {
            trySend(AuthState.Error(e))
        }
        close()
    }

    fun logout() {
        auth.signOut()
    }
}
