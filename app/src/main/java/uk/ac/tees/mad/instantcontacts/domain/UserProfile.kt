package uk.ac.tees.mad.instantcontacts.domain

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profileImageUrl: String? = null
)
