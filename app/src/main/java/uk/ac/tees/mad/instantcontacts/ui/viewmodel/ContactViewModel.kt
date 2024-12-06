package uk.ac.tees.mad.instantcontacts.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.domain.Contact
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.repository.ContactRepository

class ContactViewModel : ViewModel() {
    private val contactRepository = ContactRepository()

    private val _contactsState = MutableStateFlow<Resource<List<Contact>>>(Resource.Idle)
    val contactsState = _contactsState.asStateFlow()

    private val _addUpdateState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val addUpdateState = _addUpdateState.asStateFlow()

    private val _contactState = MutableStateFlow<Resource<Contact>>(Resource.Idle)
    val contactState = _contactState.asStateFlow()

    fun fetchContacts() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid ?: "uid"
            contactRepository.getContacts(userId).collect { resource ->
                _contactsState.value = resource
            }
        }
    }

    fun fetchContactById(contactId: String) {
        viewModelScope.launch {
            contactRepository.getContactById(contactId).collect { resource ->
                _contactState.value = resource
            }
        }
    }

    fun addContact(contact: Contact, imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri != null) {
                contactRepository.uploadImage(imageUri).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            contact.imageUrl = resource.data
                            contactRepository.addContact(contact).collect { addResource ->
                                _addUpdateState.value = addResource
                            }
                        }

                        is Resource.Error -> {
                            _addUpdateState.value = resource
                        }

                        else -> {}
                    }
                }
            } else {
                contact.imageUrl =
                    "https://ui-avatars.com/api/?name=${contact.name}+&background=random"
                contact.userId = Firebase.auth.currentUser?.uid ?: "uid"
                contactRepository.addContact(contact).collect { resource ->
                    _addUpdateState.value = resource
                }
            }
        }
    }

    fun updateContact(contact: Contact, imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri != null) {
                contactRepository.uploadImage(imageUri).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            contact.imageUrl = resource.data
                            contactRepository.updateContact(contact).collect { updateResource ->
                                _addUpdateState.value = updateResource
                            }
                        }

                        is Resource.Error -> {
                            _addUpdateState.value = resource
                        }

                        else -> {}
                    }
                }
            } else {
                contactRepository.updateContact(contact).collect { resource ->
                    _addUpdateState.value = resource
                }
            }
        }
    }

    fun addCallHistory(id: String, phone: String) {
        viewModelScope.launch {
            // TODO: To be implemented
        }
    }
}
