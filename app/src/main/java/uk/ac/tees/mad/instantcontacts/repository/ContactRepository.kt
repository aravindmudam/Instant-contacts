package uk.ac.tees.mad.instantcontacts.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.Call
import uk.ac.tees.mad.instantcontacts.domain.Contact
import java.util.UUID

class ContactRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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
                        notes = doc.getString("notes"),
                        relationship = doc.getString("relationship"),
                        callHistory = doc.get("callHistory") as? List<Call> ?: emptyList()
                    )
                } ?: emptyList()

                trySend(Resource.Success(contacts))
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getContactById(id: String): Flow<Resource<Contact>> = callbackFlow {
        trySend(Resource.Loading)
        firestore.collection("contacts").document(id).get()
            .addOnSuccessListener { result ->
                val doc = result.data
                if (doc != null) {
                    val contact = Contact(
                        id = result.id,
                        name = doc["name"] as? String ?: "",
                        phone = doc["phone"] as? String ?: "",
                        email = doc["email"] as? String,
                        imageUrl = doc["imageUrl"] as? String,
                        relationship = doc["relationship"] as? String,
                        notes = doc["notes"] as? String,
                        callHistory = doc["callHistory"] as? List<Call> ?: emptyList()
                    )
                    trySend(Resource.Success(contact))
                } else {
                    trySend(Resource.Error(Exception("Contact not found")))
                }
            }
            .addOnFailureListener { e ->
                trySend(Resource.Error(e))
            }

        awaitClose { close() }
    }

    fun addContact(contact: Contact): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)

        firestore.collection("contacts").add(contact)
            .addOnSuccessListener {
                trySend(Resource.Success("Contact added."))
            }
            .addOnFailureListener { e ->
                trySend(Resource.Error(e))
            }

        awaitClose { close() }
    }

    fun updateContact(contact: Contact): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        firestore.collection("contacts").document(contact.id).set(contact)
            .addOnSuccessListener {
                trySend(Resource.Success("Contact updated."))
            }
            .addOnFailureListener { e ->
                trySend(Resource.Error(e))
            }

        awaitClose { close() }
    }

    fun uploadImage(imageUri: Uri): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        val storageRef = storage.reference.child("contact_images/${UUID.randomUUID()}.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                trySend(Resource.Success(uri.toString()))
            }.addOnFailureListener { e ->
                trySend(Resource.Error(e))
            }
        }.addOnFailureListener { e ->
            trySend(Resource.Error(e))
        }

        awaitClose { close() }
    }
}
