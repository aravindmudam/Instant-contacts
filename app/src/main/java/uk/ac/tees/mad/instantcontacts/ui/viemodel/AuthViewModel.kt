package uk.ac.tees.mad.instantcontacts.ui.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.domain.AuthState
import uk.ac.tees.mad.instantcontacts.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registerState = MutableStateFlow<AuthState<FirebaseUser>>(AuthState.Idle)
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthState<FirebaseUser>>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password).collect { result ->
                when (result) {
                    is AuthState.Error -> {
                        _registerState.value = AuthState.Error(result.exception)
                    }
                    AuthState.Loading -> {
                        _registerState.value = AuthState.Loading
                    }
                    is AuthState.Success -> {
                        _registerState.value = AuthState.Success(result.data)
                    }

                    AuthState.Idle -> {

                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                when (result) {
                    is AuthState.Error -> {
                        _loginState.value = AuthState.Error(result.exception)
                    }
                    AuthState.Loading -> {
                        _loginState.value = AuthState.Loading
                    }
                    is AuthState.Success -> {
                        _loginState.value = AuthState.Success(result.data)
                    }

                    AuthState.Idle -> {

                    }
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
