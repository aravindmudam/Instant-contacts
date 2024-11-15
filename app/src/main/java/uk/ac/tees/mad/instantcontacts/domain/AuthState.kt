package uk.ac.tees.mad.instantcontacts.domain

sealed class AuthState<out T> {
    object Idle : AuthState<Nothing>()
    object Loading : AuthState<Nothing>()
    data class Success<T>(val data: T) : AuthState<T>()
    data class Error(val exception: Throwable) : AuthState<Nothing>()
}
