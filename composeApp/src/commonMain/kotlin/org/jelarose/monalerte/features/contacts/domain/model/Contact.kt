package org.jelarose.monalerte.features.contacts.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a contact
 */
@Serializable
data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val isSelected: Boolean = false,
    val listId: String? = null
)

/**
 * Domain model representing a contact list
 */
@Serializable
data class ContactList(
    val id: String,
    val name: String,
    val type: String,
    val isSelected: Boolean = true,
    val contacts: List<Contact> = emptyList()
)

/**
 * Types of contact lists for different alert scenarios
 */
enum class ContactListType(val key: String, val displayNameKey: String) {
    ITINERARY("ITINERARY", "contact_type_itinerary"),
    MANUAL_ALERT("PARAMETER", "contact_type_manual_alert"),
    SHARE("SHARE", "contact_type_share"),
    ACCIDENT_DETECTION("ACCIDENT", "contact_type_accident"),
    SHAKE_DETECTION("SHAKE", "contact_type_shake");
    
    companion object {
        fun fromKey(key: String): ContactListType = 
            entries.find { it.key == key } ?: MANUAL_ALERT
    }
}