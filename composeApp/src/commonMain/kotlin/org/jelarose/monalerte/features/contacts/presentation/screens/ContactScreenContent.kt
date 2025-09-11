package org.jelarose.monalerte.features.contacts.presentation.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import org.jelarose.monalerte.features.contacts.presentation.components.*
import org.jelarose.monalerte.features.contacts.presentation.viewmodel.ContactListViewModel

@Composable
fun ContactScreenContent(
    viewModel: ContactListViewModel,
    type: String,
    initialListId: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Initialize with the specified type
    LaunchedEffect(type) {
        viewModel.initContactLists(type)
        initialListId?.let { viewModel.selectList(it) }
    }
    
    // Handle back navigation
    BackHandler(
        enabled = uiState.selectedListId != null || 
                 uiState.isEditingContact || 
                 uiState.isImportingFromPhone
    ) {
        when {
            uiState.isEditingContact -> viewModel.stopEditingContact()
            uiState.isImportingFromPhone -> viewModel.stopImportingFromPhone()
            uiState.selectedListId != null -> {
                if (initialListId != null) {
                    onBack()
                } else {
                    viewModel.selectList(null)
                }
            }
        }
    }
    
    when {
        // Contact editor screen
        uiState.isEditingContact && uiState.selectedListId != null -> {
            ContactEditorScreen(
                initialContact = uiState.editingContact,
                onSave = { contact ->
                    viewModel.saveContact(contact)
                },
                onCancel = {
                    viewModel.stopEditingContact()
                }
            )
        }
        
        // Phone contact picker screen
        uiState.isImportingFromPhone && uiState.selectedListId != null -> {
            val selectedList = uiState.contactLists.firstOrNull { it.id == uiState.selectedListId }
            if (selectedList != null) {
                ContactPickerScreen(
                    contacts = emptyList(), // TODO: Load phone contacts from platform
                    alreadyInList = selectedList.contacts,
                    onConfirm = { contacts ->
                        viewModel.importContactsFromPhone(contacts)
                    },
                    onCancel = {
                        viewModel.stopImportingFromPhone()
                    }
                )
            }
        }
        
        // Contact list detail view
        uiState.selectedListId != null -> {
            val selectedList = uiState.contactLists.firstOrNull { it.id == uiState.selectedListId }
            if (selectedList != null) {
                ContactListDetailView(
                    list = selectedList,
                    onBack = {
                        if (initialListId != null) {
                            onBack()
                        } else {
                            viewModel.selectList(null)
                        }
                    },
                    onUpdateList = { updatedList ->
                        viewModel.renameList(updatedList.id, updatedList.name)
                    },
                    onUpdateContact = { contact ->
                        viewModel.startEditingContact(contact)
                    },
                    onDeleteContact = { contactId ->
                        viewModel.deleteContact(contactId)
                    },
                    onToggleContact = { contactId ->
                        viewModel.toggleContactSelection(contactId)
                    },
                    onAddContact = {
                        viewModel.startEditingContact(null)
                    },
                    onImportFromPhone = {
                        viewModel.startImportingFromPhone()
                    }
                )
            }
        }
        
        // Main contact list manager view
        else -> {
            ContactListManagerView(
                contactLists = uiState.contactLists,
                onOpenListDetail = { list ->
                    viewModel.selectList(list.id)
                },
                onToggleList = { listId ->
                    viewModel.toggleListSelection(listId)
                },
                onAddList = { name ->
                    viewModel.addList(name)
                },
                onDeleteList = { listId ->
                    viewModel.deleteList(listId)
                },
                type = type,
                onBack = onBack,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onClearError = viewModel::clearError
            )
        }
    }
}

/**
 * BackHandler composable for handling back navigation
 * This is a simplified version - in production, use the actual BackHandler from activity-compose
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)