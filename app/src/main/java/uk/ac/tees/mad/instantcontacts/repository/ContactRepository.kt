package uk.ac.tees.mad.instantcontacts.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.data.ContactDao
import uk.ac.tees.mad.instantcontacts.domain.Call
import uk.ac.tees.mad.instantcontacts.domain.Contact
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.toContactEntity
import java.util.UUID
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val contactDao: ContactDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    fun getContacts(userId: String): Flow<Resource<List<Contact>>> = callbackFlow {
        trySend(Resource.Loading)

        // First, fetch from local database
        val cachedContacts = contactDao.getContacts(userId).map { entities ->
            entities.map { it.toContact() }
        }
        trySend(Resource.Success(cachedContacts.first()))

        // Then, listen for changes from Firestore
        val listenerRegistration = firestore.collection("contacts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error))
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
                        userId = doc.getString("userId"),
                        relationship = doc.getString("relationship"),
                        callHistory = (doc["callHistory"] as? List<HashMap<String, Any>>)?.map { map ->
                            Call(
                                timestamp = map["timestamp"] as? String ?: "",
                                duration = (map["duration"] as? Long) ?: 0L
                            )
                        } ?: emptyList()
                    )
                } ?: emptyList()

                CoroutineScope(ioDispatcher).launch {
                    // Update the local cache with the latest data
                    contactDao.insertContacts(contacts.map { it.toContactEntity() })
                }

                trySend(Resource.Success(contacts))
            }

        awaitClose { listenerRegistration.remove() }
    }.flowOn(ioDispatcher)


    fun getContactById(id: String): Flow<Resource<Contact>> = callbackFlow {
        trySend(Resource.Loading)

        // Launch a coroutine to handle database operations
        CoroutineScope(ioDispatcher).launch {
            val cachedContact = contactDao.getContactById(id).first()?.toContact()
            cachedContact?.let { trySend(Resource.Success(it)) }
        }

        // Fetch from Firestore
        firestore.collection("contacts").document(id).get()
            .addOnSuccessListener { result ->
                val doc = result.data
                if (doc != null) {
                    val callHistory =
                        (doc["callHistory"] as? List<HashMap<String, Any>>)?.map { map ->
                            Call(
                                timestamp = map["timestamp"] as? String ?: "",
                                duration = (map["duration"] as? Long) ?: 0L
                            )
                        } ?: emptyList()

                    val contact = Contact(
                        id = result.id,
                        name = doc["name"] as? String ?: "",
                        phone = doc["phone"] as? String ?: "",
                        email = doc["email"] as? String,
                        imageUrl = doc["imageUrl"] as? String,
                        userId = doc["userId"] as? String,
                        relationship = doc["relationship"] as? String,
                        notes = doc["notes"] as? String,
                        callHistory = callHistory
                    )

                    // Update the local cache with the latest data
                    CoroutineScope(ioDispatcher).launch {
                        contactDao.insertContact(contact.toContactEntity())
                    }

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

        val contactEntity = contact.toContactEntity()
        try {
            contactDao.insertContact(contactEntity)
        } catch (e: Exception) {
            trySend(Resource.Error(e))
            close(e)
            return@callbackFlow
        }

        firestore.collection("contacts").add(contact)
            .addOnSuccessListener {
                trySend(Resource.Success("Contact added."))
            }
            .addOnFailureListener { e ->
                CoroutineScope(ioDispatcher).launch {
                    contactDao.deleteContactById(contactEntity.id)
                }
                trySend(Resource.Error(e))
            }

        awaitClose { close() }
    }


    fun updateContact(contact: Contact): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)

        val contactEntity = contact.toContactEntity()
        try {
            contactDao.insertContact(contactEntity)
        } catch (e: Exception) {
            trySend(Resource.Error(e))
            close(e)
            return@callbackFlow
        }

        firestore.collection("contacts").document(contact.id).set(contact)
            .addOnSuccessListener {
                trySend(Resource.Success("Contact updated."))
            }
            .addOnFailureListener { e ->
                trySend(Resource.Error(e))
            }

        awaitClose { close() }
    }


    fun deleteContact(contactId: String): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        firestore.collection("contacts").document(contactId).delete()
            .addOnSuccessListener {
                CoroutineScope(ioDispatcher).launch {
                    contactDao.deleteContactById(contactId)
                }
                trySend(Resource.Success("Contact deleted successfully."))
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
