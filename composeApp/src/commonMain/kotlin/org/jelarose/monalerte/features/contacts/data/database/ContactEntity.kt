package org.jelarose.monalerte.features.contacts.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.datetime.Clock

/**
 * Database entity for contact lists
 */
@Entity(
    tableName = "contact_lists"
)
data class ContactListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val isSelected: Boolean = true,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

/**
 * Database entity for contacts
 */
@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = ContactListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"])]
)
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val isSelected: Boolean = false,
    val listId: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)