package uk.ac.tees.mad.instantcontacts.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.UserProfile
import uk.ac.tees.mad.instantcontacts.repository.ContactRepository
import uk.ac.tees.mad.instantcontacts.repository.ProfileRepository

class ProfileViewModel() : ViewModel() {
    private val profileRepository = ProfileRepository()
    private val contactRepository = ContactRepository()

    private val _profileState = MutableStateFlow<Resource<UserProfile>>(Resource.Idle)
    val profileState = _profileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val updateProfileState = _updateProfileState.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            profileRepository.getUserProfile().collect { resource ->
                _profileState.value = resource
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun updateProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            profileRepository.updateUserProfile(userProfile).collect { resource ->
                _updateProfileState.value = resource
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        contactRepository.uploadImage(imageUri).collect { resource ->
            emit(resource)
        }
    }
}
