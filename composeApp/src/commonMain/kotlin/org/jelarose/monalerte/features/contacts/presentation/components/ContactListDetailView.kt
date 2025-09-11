package org.jelarose.monalerte.features.contacts.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.core.utils.localizedString
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import org.jelarose.monalerte.features.contacts.domain.model.ContactList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListDetailView(
    list: ContactList,
    onBack: () -> Unit,
    onUpdateList: (ContactList) -> Unit,
    onUpdateContact: (Contact) -> Unit,
    onDeleteContact: (String) -> Unit,
    onToggleContact: (String) -> Unit,
    onAddContact: () -> Unit,
    onImportFromPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingListName by remember { mutableStateOf(false) }
    var editedListName by remember(list.name) { mutableStateOf(list.name) }
    var showDeleteContactDialog by remember { mutableStateOf<String?>(null) }
    var selectAllState by remember { mutableStateOf(false) }
    
    // Calculer l'état "tout sélectionné"
    LaunchedEffect(list.contacts.map { it.isSelected }) {
        val allSelected = list.contacts.isNotEmpty() && list.contacts.all { it.isSelected }
        if (selectAllState != allSelected) {
            selectAllState = allSelected
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isEditingListName) {
                        TextField(
                            value = editedListName,
                            onValueChange = { editedListName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (editedListName.isNotBlank() && editedListName != list.name) {
                                        onUpdateList(list.copy(name = editedListName))
                                    }
                                    isEditingListName = false
                                }
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    } else {
                        Text(
                            text = list.name,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            "←",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    if (isEditingListName) {
                        IconButton(
                            onClick = {
                                if (editedListName.isNotBlank() && editedListName != list.name) {
                                    onUpdateList(list.copy(name = editedListName))
                                }
                                isEditingListName = false
                            }
                        ) {
                            Text(
                                "✓",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        IconButton(onClick = { isEditingListName = true }) {
                            Text(
                                "✏️",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
            // Section boutons d'actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddContact,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("+ ${localizedString("add_contact")}")
                }
                
                Button(
                    onClick = onImportFromPhone,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("📱 ${localizedString("import_from_phone")}")
                }
            }
            
            // Section sélection globale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        // Toggle all contacts
                        list.contacts.forEach { contact ->
                            onToggleContact(contact.id)
                        }
                    }
                ) {
                    Checkbox(
                        checked = selectAllState,
                        onCheckedChange = { value ->
                            selectAllState = value
                            list.contacts.forEach { contact ->
                                if (contact.isSelected != value) {
                                    onToggleContact(contact.id)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localizedString("select_all"),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "${list.contacts.count { it.isSelected }}/${list.contacts.size} sélectionnés",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (list.contacts.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateMessage(
                        message = localizedString("no_contacts")
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(list.contacts, key = { _, contact -> contact.id }) { index, contact ->
                        ContactCard(
                            contact = contact,
                            onToggle = { onToggleContact(contact.id) },
                            onEdit = { onUpdateContact(contact) },
                            onDelete = { showDeleteContactDialog = contact.id }
                        )
                    }
                }
            }
        }
    }
    
    // Delete contact confirmation dialog
    showDeleteContactDialog?.let { contactId ->
        AlertDialog(
            onDismissRequest = { showDeleteContactDialog = null },
            title = { Text(localizedString("confirm_delete")) },
            text = { Text(localizedString("confirm_delete_contact")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteContact(contactId)
                        showDeleteContactDialog = null
                    }
                ) {
                    Text(localizedString("delete"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteContactDialog = null }) {
                    Text(localizedString("cancel"))
                }
            }
        )
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = contact.isSelected,
                onCheckedChange = { onToggle() }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = contact.phone,
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
private fun EmptyStateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👥",
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