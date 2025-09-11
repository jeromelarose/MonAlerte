package org.jelarose.monalerte.features.contacts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi
import org.jelarose.monalerte.core.utils.localizedString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun ContactPickerScreen(
    contacts: List<Contact>,
    alreadyInList: List<Contact>,
    onConfirm: (List<Contact>) -> Unit,
    onCancel: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedContacts = remember { mutableStateMapOf<String, Boolean>() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchQueryClean = searchQuery.trim().lowercase()
    val existingKeys = remember(alreadyInList) {
        alreadyInList.map { (it.name + it.phone).trim().lowercase() }.toSet()
    }

    val filteredContacts = remember(contacts, existingKeys, searchQueryClean) {
        contacts
            .filter {
                val key = (it.name + it.phone).trim().lowercase()
                !existingKeys.contains(key)
            }
            .filter {
                it.name.lowercase().contains(searchQueryClean) ||
                        it.phone.lowercase().contains(searchQueryClean)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedString("contact_picker_title_text")) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(localizedString("contact_picker_search_label_text")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredContacts, key = { it.id }) { contact ->
                    val isChecked = selectedContacts[contact.id] == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedContacts[contact.id] = !isChecked },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { selectedContacts[contact.id] = it },
                            )
                            Column(modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)) {
                                Text(
                                    contact.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    contact.phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        localizedString("contact_picker_cancel_button_text"),
                        maxLines = 1
                    )
                }

                Button(
                    onClick = {
                        val selected = contacts
                            .filter {
                                val key = (it.name + it.phone).trim().lowercase()
                                !existingKeys.contains(key)
                            }
                            .filter {
                                selectedContacts[it.id] == true
                            }
                        onConfirm(selected)
                    },
                    enabled = selectedContacts.values.any { it },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "${localizedString("contact_picker_import_button_text")} (${selectedContacts.values.count { it }})",
                        maxLines = 1
                    )
                }
            }
        }
    }
}