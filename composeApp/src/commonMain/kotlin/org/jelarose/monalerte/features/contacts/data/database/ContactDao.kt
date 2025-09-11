package org.jelarose.monalerte.features.contacts.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Dao
interface ContactDao {
    // Contact Lists operations
    @Insert
    suspend fun insertList(list: ContactListEntity)
    
    @Update
    suspend fun updateList(list: ContactListEntity)
    
    @Delete
    suspend fun deleteList(list: ContactListEntity)
    
    @Query("SELECT * FROM contact_lists WHERE type = :type ORDER BY name ASC")
    fun getListsByType(type: String): Flow<List<ContactListEntity>>
    
    @Query("SELECT * FROM contact_lists WHERE id = :listId")
    suspend fun getListById(listId: String): ContactListEntity?
    
    @Query("UPDATE contact_lists SET isSelected = :isSelected WHERE id = :listId")
    suspend fun updateListSelection(listId: String, isSelected: Boolean)
    
    @Query("UPDATE contact_lists SET name = :newName, updatedAt = :updatedAt WHERE id = :listId")
    suspend fun renameList(listId: String, newName: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())
    
    // Contacts operations
    @Insert
    suspend fun insertContact(contact: ContactEntity)
    
    @Update
    suspend fun updateContact(contact: ContactEntity)
    
    @Delete
    suspend fun deleteContact(contact: ContactEntity)
    
    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContactById(contactId: String)
    
    @Query("SELECT * FROM contacts WHERE listId = :listId ORDER BY name ASC")
    fun getContactsByListId(listId: String): Flow<List<ContactEntity>>
    
    @Query("UPDATE contacts SET isSelected = :isSelected WHERE id = :contactId")
    suspend fun updateContactSelection(contactId: String, isSelected: Boolean)
    
    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: String): ContactEntity?
    
    @Query("UPDATE contacts SET isSelected = NOT isSelected WHERE id = :contactId")
    suspend fun toggleContactSelection(contactId: String)
    
    // Relations
    @Transaction
    @Query("SELECT * FROM contact_lists WHERE type = :type ORDER BY name ASC")
    fun getListsWithContactsByType(type: String): Flow<List<ContactListWithContacts>>
    
    @Transaction
    @Query("SELECT * FROM contact_lists WHERE id = :listId")
    suspend fun getListWithContacts(listId: String): ContactListWithContacts?
    
    @Transaction
    @Query("SELECT * FROM contact_lists WHERE isSelected = 1")
    fun getSelectedListsWithContacts(): Flow<List<ContactListWithContacts>>
    
    // Utility queries
    @Query("SELECT COUNT(*) FROM contacts WHERE listId = :listId")
    suspend fun getContactCountForList(listId: String): Int
    
    @Query("SELECT * FROM contacts WHERE isSelected = 1 AND listId IN (SELECT id FROM contact_lists WHERE isSelected = 1 AND type = :type)")
    fun getSelectedContactsByType(type: String): Flow<List<ContactEntity>>
}