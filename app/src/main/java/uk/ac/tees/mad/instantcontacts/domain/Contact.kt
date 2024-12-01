package uk.ac.tees.mad.instantcontacts.domain

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    var userId: String? = null,
    var imageUrl: String? = null,
    val notes: String? = null,
    val callHistory: List<Call> = emptyList()
)

data class Call(
    val timestamp: String,
    val duration: Long,
)