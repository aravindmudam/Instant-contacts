package uk.ac.tees.mad.instantcontacts.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.instantcontacts.data.ContactDao
import uk.ac.tees.mad.instantcontacts.data.ContactsDatabase
import uk.ac.tees.mad.instantcontacts.repository.AuthRepository
import uk.ac.tees.mad.instantcontacts.repository.ContactRepository
import uk.ac.tees.mad.instantcontacts.repository.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesAuthentication(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideContactRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        dao: ContactDao
    ): ContactRepository = ContactRepository(firestore, storage, dao)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepository(auth, firestore)

    @Provides
    @Singleton
    fun provideProfileRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): ProfileRepository = ProfileRepository(firestore, auth)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ContactsDatabase =
        Room.databaseBuilder(context, ContactsDatabase::class.java, "contacts_database").build()

    @Provides
    @Singleton
    fun providesDao(database: ContactsDatabase) = database.contactDao()

}