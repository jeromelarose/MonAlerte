package org.jelarose.monalerte.features.contacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.core.utils.localizedString
import org.jelarose.monalerte.features.contacts.domain.model.ContactList
import org.jelarose.monalerte.features.contacts.domain.model.ContactListType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListManagerView(
    contactLists: List<ContactList>,
    onOpenListDetail: (ContactList) -> Unit,
    onToggleList: (String) -> Unit,
    onAddList: (String) -> Unit,
    onDeleteList: (String) -> Unit,
    type: String,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddListDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<String?>(null) }
    
    val listType = ContactListType.fromKey(type)
    val title = localizedString(listType.displayNameKey)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AddListSection(
                onAdd = { name -> onAddList(name) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Compteur de contacts sélectionnés
            val selectedContactsCount = contactLists
                .filter { it.isSelected }
                .sumOf { list -> list.contacts.count { it.isSelected } }
            val totalContactsCount = contactLists
                .filter { it.isSelected }
                .sumOf { it.contacts.size }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$selectedContactsCount/$totalContactsCount sélectionnés",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                error != null -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorMessage(
                            message = error,
                            onDismiss = onClearError
                        )
                    }
                }
                
                contactLists.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateMessage(
                            message = localizedString("no_contact_lists")
                        )
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(contactLists, key = { _, list -> list.id }) { index, list ->
                            ContactListCard(
                                list = list,
                                onToggle = { onToggleList(list.id) },
                                onClick = { onOpenListDetail(list) },
                                onDelete = { showDeleteConfirmDialog = list.id }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add list dialog
    if (showAddListDialog) {
        AddListDialog(
            onConfirm = { name ->
                onAddList(name)
                showAddListDialog = false
            },
            onDismiss = { showAddListDialog = false }
        )
    }
    
    // Delete confirmation dialog
    showDeleteConfirmDialog?.let { listId ->
        DeleteConfirmationDialog(
            message = localizedString("confirm_delete_list"),
            onConfirm = {
                onDeleteList(listId)
                showDeleteConfirmDialog = null
            },
            onDismiss = { showDeleteConfirmDialog = null }
        )
    }
}

@Composable
private fun AddListSection(
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    
    Button(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("+")
        Spacer(modifier = Modifier.width(8.dp))
        Text(localizedString("add_contact_list"))
    }
    
    if (showDialog) {
        AddListDialog(
            onConfirm = { name ->
                onAdd(name)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ContactListCard(
    list: ContactList,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalInList = list.contacts.size
    val selectedInList = list.contacts.count { it.isSelected }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Checkbox(
                checked = list.isSelected,
                onCheckedChange = { onToggle() }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$selectedInList/$totalInList sélectionnés",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Text(
                    "🗑️",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddListDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var listName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString("add_contact_list")) },
        text = {
            OutlinedTextField(
                value = listName,
                onValueChange = { listName = it },
                label = { Text(localizedString("list_name")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (listName.isNotBlank()) {
                        onConfirm(listName)
                    }
                },
                enabled = listName.isNotBlank()
            ) {
                Text(localizedString("add"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedString("cancel"))
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString("confirm_delete")) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(localizedString("delete"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedString("cancel"))
            }
        }
    )
}

@Composable
private fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📋",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text(localizedString("dismiss"))
            }
        }
    }
}