package org.jelarose.monalerte.features.contacts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import org.jelarose.monalerte.features.contacts.domain.model.ContactList
import org.jelarose.monalerte.features.contacts.domain.repository.ContactRepository
import kotlin.uuid.Uuid

data class ContactListUiState(
    val contactLists: List<ContactList> = emptyList(),
    val selectedListId: String? = null,
    val editingContact: Contact? = null,
    val isEditingContact: Boolean = false,
    val isImportingFromPhone: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ContactListViewModel(
    private val repository: ContactRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ContactListUiState())
    val uiState: StateFlow<ContactListUiState> = _uiState.asStateFlow()
    
    private var currentType: String = ""
    
    fun initContactLists(typeArgument: String) {
        currentType = typeArgument
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllListsWithContactsByType(typeArgument)
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { lists ->
                    _uiState.update { 
                        it.copy(
                            contactLists = lists,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
    
    fun selectList(listId: String?) {
        _uiState.update { it.copy(selectedListId = listId) }
    }
    
    fun startEditingContact(contact: Contact? = null) {
        _uiState.update { 
            it.copy(
                editingContact = contact,
                isEditingContact = true
            )
        }
    }
    
    fun stopEditingContact() {
        _uiState.update { 
            it.copy(
                editingContact = null,
                isEditingContact = false
            )
        }
    }
    
    fun startImportingFromPhone() {
        _uiState.update { it.copy(isImportingFromPhone = true) }
    }
    
    fun stopImportingFromPhone() {
        _uiState.update { it.copy(isImportingFromPhone = false) }
    }
    
    fun addList(name: String) {
        viewModelScope.launch {
            try {
                repository.addList(currentType, name)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun renameList(listId: String, newName: String) {
        viewModelScope.launch {
            try {
                repository.renameList(listId, newName)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                repository.deleteList(listId)
                _uiState.update { it.copy(selectedListId = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleListSelection(listId: String) {
        viewModelScope.launch {
            try {
                repository.toggleListSelection(listId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun addContactToList(listId: String, contact: Contact) {
        viewModelScope.launch {
            try {
                // Check if contact already exists (by phone number)
                val existingContact = _uiState.value.contactLists
                    .find { it.id == listId }
                    ?.contacts
                    ?.firstOrNull { it.phone == contact.phone }
                
                if (existingContact == null) {
                    repository.addContactToList(listId, contact)
                    stopEditingContact() // Return to contact list after adding new contact
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            try {
                repository.updateContact(contact)
                stopEditingContact() // Return to contact list after updating contact
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            try {
                repository.deleteContact(contactId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleContactSelection(contactId: String) {
        viewModelScope.launch {
            try {
                repository.toggleContactSelection(contactId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun saveContact(contact: Contact) {
        val selectedListId = _uiState.value.selectedListId ?: return
        
        if (_uiState.value.editingContact == null) {
            // Adding new contact
            addContactToList(selectedListId, contact.copy(listId = selectedListId))
        } else {
            // Updating existing contact
            updateContact(contact.copy(listId = selectedListId))
        }
    }
    
    fun importContactsFromPhone(contacts: List<Contact>) {
        val selectedListId = _uiState.value.selectedListId ?: return
        
        viewModelScope.launch {
            contacts.forEach { contact ->
                addContactToList(selectedListId, contact)
            }
            stopImportingFromPhone()
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}