package org.jelarose.monalerte.features.contacts.data.database

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relation between a contact list and its contacts
 */
data class ContactListWithContacts(
    @Embedded val list: ContactListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val contacts: List<ContactEntity>
)