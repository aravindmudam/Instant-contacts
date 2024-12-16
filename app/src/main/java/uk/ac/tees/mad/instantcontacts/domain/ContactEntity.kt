package uk.ac.tees.mad.instantcontacts.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val imageUrl: String?,
    val userId: String?,
    val relationship: String?,
    val notes: String?,
    val callHistory: List<Call>
) {
    fun toContact(): Contact = Contact(
        id = id,
        name = name,
        phone = phone,
        email = email,
        imageUrl = imageUrl,
        userId = userId,
        relationship = relationship,
        notes = notes,
        callHistory = callHistory
    )
}

fun Contact.toContactEntity(): ContactEntity = ContactEntity(
    id = id,
    name = name,
    phone = phone,
    email = email,
    imageUrl = imageUrl,
    userId = userId,
    relationship = relationship,
    notes = notes,
    callHistory = callHistory
)
