package org.jelarose.monalerte.features.contacts.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.jelarose.monalerte.features.contacts.data.database.*
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import org.jelarose.monalerte.features.contacts.domain.model.ContactList
import org.jelarose.monalerte.features.contacts.domain.repository.ContactRepository
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)

class ContactRepositoryImpl(
    private val contactDao: ContactDao
) : ContactRepository {
    
    override suspend fun addList(type: String, name: String): String {
        val listId = Uuid.random().toString()
        val list = ContactListEntity(
            id = listId,
            name = name,
            type = type,
            isSelected = true
        )
        contactDao.insertList(list)
        return listId
    }
    
    override suspend fun renameList(listId: String, newName: String) {
        contactDao.renameList(listId, newName)
    }
    
    override suspend fun deleteList(listId: String) {
        contactDao.getListById(listId)?.let { list ->
            contactDao.deleteList(list)
        }
    }
    
    override suspend fun toggleListSelection(listId: String) {
        contactDao.getListById(listId)?.let { list ->
            contactDao.updateListSelection(listId, !list.isSelected)
        }
    }
    
    override fun getAllListsWithContactsByType(type: String): Flow<List<ContactList>> {
        return contactDao.getListsWithContactsByType(type).map { relations ->
            relations.map { relation ->
                ContactList(
                    id = relation.list.id,
                    name = relation.list.name,
                    type = relation.list.type,
                    isSelected = relation.list.isSelected,
                    contacts = relation.contacts.map { entity ->
                        Contact(
                            id = entity.id,
                            name = entity.name,
                            phone = entity.phone,
                            isSelected = entity.isSelected,
                            listId = entity.listId
                        )
                    }
                )
            }
        }
    }
    
    override suspend fun addContactToList(listId: String, contact: Contact): String {
        val contactId = contact.id.ifEmpty { Uuid.random().toString() }
        val entity = ContactEntity(
            id = contactId,
            name = contact.name,
            phone = contact.phone,
            isSelected = contact.isSelected,
            listId = listId
        )
        contactDao.insertContact(entity)
        return contactId
    }
    
    override suspend fun updateContact(contact: Contact) {
        contact.listId?.let { listId ->
            val entity = ContactEntity(
                id = contact.id,
                name = contact.name,
                phone = contact.phone,
                isSelected = contact.isSelected,
                listId = listId,
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            contactDao.updateContact(entity)
        }
    }
    
    override suspend fun deleteContact(contactId: String) {
        contactDao.deleteContactById(contactId)
    }
    
    override suspend fun toggleContactSelection(contactId: String) {
        contactDao.toggleContactSelection(contactId)
    }
    
    override fun getSelectedContactsByType(type: String): Flow<List<Contact>> {
        return contactDao.getSelectedContactsByType(type).map { entities ->
            entities.map { entity ->
                Contact(
                    id = entity.id,
                    name = entity.name,
                    phone = entity.phone,
                    isSelected = entity.isSelected,
                    listId = entity.listId
                )
            }
        }
    }
    
    override suspend fun getContactCountForList(listId: String): Int {
        return contactDao.getContactCountForList(listId)
    }
}