package org.jelarose.monalerte.features.contacts.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.features.contacts.domain.model.Contact
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun ContactEditorScreen(
    initialContact: Contact? = null,
    onSave: (Contact) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var phoneHasBeenFocused by remember { mutableStateOf(false) }

    val titleText = if (initialContact == null)
        "Nouveau contact" // contact_editor_new_contact_title
    else
        "Modifier le contact" // contact_editor_edit_contact_title

    // Validation du téléphone exactement comme l'original
    val isPhoneValid = remember(phone) {
        val normalizedPhone = phone.replace("\\s+".toRegex(), "")
        if (normalizedPhone.isBlank()) {
            true
        } else {
            normalizedPhone.matches(Regex("""^(\+33|0)[1-9]\d{8}$"""))
        }
    }
    val displayPhoneError = phoneHasBeenFocused && phone.isNotBlank() && !isPhoneValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText) },
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") }, // contact_editor_name_label_text
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    if (!phoneHasBeenFocused) phoneHasBeenFocused = true
                },
                isError = displayPhoneError,
                label = { Text("Téléphone") }, // contact_editor_phone_label_text
                supportingText = {
                    if (displayPhoneError) {
                        Text("Format : +33 6 12 34 56 78 ou 06 12 34 56 78") // contact_editor_phone_error_text
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Annuler", // common_cancel
                        maxLines = 1
                    )
                }

                Button(
                    onClick = {
                        onSave(
                            Contact(
                                id = initialContact?.id ?: Uuid.random().toString(),
                                name = name.trim(),
                                phone = phone.trim(),
                                isSelected = initialContact?.isSelected ?: false,
                                listId = initialContact?.listId
                            )
                        )
                    },
                    enabled = name.isNotBlank() && isPhoneValid && phone.isNotBlank(),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Enregistrer", // contact_editor_save_button_text
                        maxLines = 1
                    )
                }
            }
        }
    }
}