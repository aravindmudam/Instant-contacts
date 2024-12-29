package uk.ac.tees.mad.instantcontacts.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.instantcontacts.domain.Resource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth : FirebaseAuth,
    private val firestore : FirebaseFirestore,

) {
    private val userId = auth.currentUser?.uid ?: ""

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            user?.let { u ->
                firestore.collection("users").document(u.uid)
                    .set(mapOf("name" to name, "email" to email))
            }

            trySend(Resource.Success(user!!))
        } catch (e: Exception) {
            trySend(Resource.Error(e))
        }
        close()
    }

    fun login(
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser>> = callbackFlow {
        trySend(Resource.Loading)
        try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            trySend(Resource.Success(user!!))
        } catch (e: Exception) {
            trySend(Resource.Error(e))
        }
        close()
    }

    fun logout() {
        auth.signOut()
    }
}
