package uk.ac.tees.mad.instantcontacts.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.UserProfile


class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserProfile(): Flow<Resource<UserProfile>> = callbackFlow {
        trySend(Resource.Loading)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { result ->
                    val data = result.data
                    if (data != null) {
                        val userProfile = UserProfile(
                            id = result.id,
                            name = data["name"] as? String ?: "",
                            email = data["email"] as? String ?: "",
                            profileImageUrl = data["profileImageUrl"] as? String,
                            phone = data["phone"] as? String ?: ""
                        )
                        trySend(Resource.Success(userProfile))
                    } else {
                        trySend(Resource.Error(Exception("User not found")))
                    }
                }
                .addOnFailureListener { e ->
                    trySend(Resource.Error(e))
                }
        } else {
            trySend(Resource.Error(Exception("User is not authenticated")))
        }

        awaitClose { close() }
    }

    fun updateUserProfile(userProfile: UserProfile): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).set(userProfile)
                .addOnSuccessListener {
                    trySend(Resource.Success("Profile updated successfully"))
                }
                .addOnFailureListener { e ->
                    trySend(Resource.Error(e))
                }
        } else {
            trySend(Resource.Error(Exception("User is not authenticated")))
        }

        awaitClose { close() }
    }
}
