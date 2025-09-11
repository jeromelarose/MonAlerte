package org.jelarose.monalerte.features.contacts.domain.repository

import kotlinx.coroutines.flow.Flow
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import org.jelarose.monalerte.features.contacts.domain.model.ContactList

interface ContactRepository {
    // List operations
    suspend fun addList(type: String, name: String): String
    suspend fun renameList(listId: String, newName: String)
    suspend fun deleteList(listId: String)
    suspend fun toggleListSelection(listId: String)
    fun getAllListsWithContactsByType(type: String): Flow<List<ContactList>>
    
    // Contact operations
    suspend fun addContactToList(listId: String, contact: Contact): String
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contactId: String)
    suspend fun toggleContactSelection(contactId: String)
    
    // Query operations
    fun getSelectedContactsByType(type: String): Flow<List<Contact>>
    suspend fun getContactCountForList(listId: String): Int
}