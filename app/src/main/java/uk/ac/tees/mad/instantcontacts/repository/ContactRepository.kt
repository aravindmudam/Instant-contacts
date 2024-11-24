package uk.ac.tees.mad.instantcontacts.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.Call
import uk.ac.tees.mad.instantcontacts.domain.Contact

class ContactRepository {
    private val firestore = Firebase.firestore

    fun getContacts(userId: String): Flow<Resource<List<Contact>>> = callbackFlow {
        trySend(Resource.Loading)
        val listenerRegistration = firestore.collection("contacts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error))
                    close(error)
                    return@addSnapshotListener
                }

                val contacts = snapshot?.documents?.map { doc ->
                    Contact(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email"),
                        imageUrl = doc.getString("imageUrl"),
                        medicalInfo = doc.getString("medicalInfo"),
                        callHistory = doc.get("callHistory") as? List<Call> ?: emptyList()
                    )
                } ?: emptyList()

                trySend(Resource.Success(contacts))
            }

        awaitClose { listenerRegistration.remove() }
    }

}