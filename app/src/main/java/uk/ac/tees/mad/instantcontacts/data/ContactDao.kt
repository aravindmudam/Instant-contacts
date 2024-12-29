package uk.ac.tees.mad.instantcontacts.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.instantcontacts.domain.ContactEntity

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE userId = :userId")
    fun getContacts(userId: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: String): Flow<ContactEntity?>

    @Upsert
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Upsert
    suspend fun insertContact(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: String)
}
