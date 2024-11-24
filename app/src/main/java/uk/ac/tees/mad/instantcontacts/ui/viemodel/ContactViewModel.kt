package uk.ac.tees.mad.instantcontacts.ui.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchContacts(userId: String) {
        viewModelScope.launch {
            contactRepository.getContacts(userId).collect { resource ->
                _contactsState.value = resource
            }
        }
    }
}