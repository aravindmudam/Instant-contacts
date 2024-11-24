package uk.ac.tees.mad.instantcontacts.ui.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registerState = MutableStateFlow<Resource<FirebaseUser>>(Resource.Idle)
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow<Resource<FirebaseUser>>(Resource.Idle)
    val loginState = _loginState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _registerState.value = Resource.Error(result.exception)
                    }
                    Resource.Loading -> {
                        _registerState.value = Resource.Loading
                    }
                    is Resource.Success -> {
                        _registerState.value = Resource.Success(result.data)
                    }

                    Resource.Idle -> {

                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _loginState.value = Resource.Error(result.exception)
                    }
                    Resource.Loading -> {
                        _loginState.value = Resource.Loading
                    }
                    is Resource.Success -> {
                        _loginState.value = Resource.Success(result.data)
                    }

                    Resource.Idle -> {

                    }
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
