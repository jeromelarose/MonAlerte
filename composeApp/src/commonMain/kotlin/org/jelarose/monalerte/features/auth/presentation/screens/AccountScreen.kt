package org.jelarose.monalerte.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import monalerte.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jelarose.monalerte.features.auth.presentation.viewmodels.AccountViewModel

/**
 * Écran de gestion du compte - réplique exacte de AccountSettingsScreen
 * Permet de changer le mot de passe, gérer la langue et se déconnecter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onBackClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // États du ViewModel
    val userEmail by viewModel.userEmail.collectAsState()
    val oldPassword by viewModel.oldPassword.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmNewPassword by viewModel.confirmNewPassword.collectAsState()
    
    val oldPasswordError by viewModel.oldPasswordError.collectAsState()
    val newPasswordError by viewModel.newPasswordError.collectAsState()
    val confirmNewPasswordError by viewModel.confirmNewPasswordError.collectAsState()
    
    val isChangePasswordFormValid by viewModel.isChangePasswordFormValid.collectAsState()
    val isLoadingChangePassword by viewModel.isLoadingChangePassword.collectAsState()
    val changePasswordApiError by viewModel.changePasswordApiError.collectAsState()
    val changePasswordSuccess by viewModel.changePasswordSuccess.collectAsState()
    val isLoadingLogout by viewModel.isLoadingLogout.collectAsState()

    // États locaux pour la visibilité des mots de passe
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }
    
    // États pour les dialogues
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLanguageSelectionDialog by remember { mutableStateOf(false) }
    
    // Langue actuelle (simplifiée pour cet exemple)
    var currentLanguage by remember { mutableStateOf("Français") }

    Scaffold(
        modifier = Modifier.semantics {
            testTag = "account_scaffold"
            contentDescription = "Écran Mon Compte"
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(Res.string.auth_my_account),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.semantics {
                            testTag = "account_title"
                            contentDescription = "Titre Mon Compte"
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.semantics {
                            testTag = "account_back_button"
                            contentDescription = "Bouton Retour"
                        }
                    ) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.semantics {
                    testTag = "account_topbar"
                    contentDescription = "Barre de Titre Mon Compte"
                }
            )
        }
    ) { paddingValues ->
        // Arrière-plan avec dégradé subtil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .semantics {
                        testTag = "account_content"
                        contentDescription = "Contenu Mon Compte"
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.auth_account_information),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .semantics {
                            testTag = "account_information_title"
                            contentDescription = "Titre Informations du compte"
                        }
                )

                InfoRow(
                    label = stringResource(Res.string.auth_email),
                    value = userEmail
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section Paramètres de langue
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            testTag = "language_settings_card"
                            contentDescription = "Paramètres de langue"
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.language_settings_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = stringResource(Res.string.language_settings_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedButton(
                            onClick = { showLanguageSelectionDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🌐",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = stringResource(
                                    Res.string.language_current_selection,
                                    currentLanguage
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton Changer le mot de passe
                Button(
                    onClick = {
                        viewModel.clearChangePasswordFieldsAndErrors()
                        showChangePasswordDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            testTag = "change_password_button"
                            contentDescription = "Bouton Changer le mot de passe"
                        }
                ) {
                    Text(stringResource(Res.string.auth_change_password))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton Déconnexion
                OutlinedButton(
                    onClick = {
                        viewModel.logout {
                            onLogoutSuccess()
                        }
                    },
                    enabled = !isLoadingLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            testTag = "logout_button"
                            contentDescription = "Bouton Se déconnecter"
                        }
                ) {
                    if (isLoadingLogout) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(stringResource(Res.string.auth_button_disconnected))
                }
            }
        }
    }

    // Dialogue de changement de mot de passe
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { 
                Text(
                    text = stringResource(Res.string.settings_change_password_dialog_title),
                    modifier = Modifier.semantics {
                        testTag = "change_password_dialog_title"
                    }
                ) 
            },
            text = {
                Column {
                    // Ancien mot de passe
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { viewModel.onOldPasswordChange(it) },
                        label = { Text(stringResource(Res.string.settings_old_password_label)) },
                        visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, 
                            imeAction = ImeAction.Next
                        ),
                        isError = oldPasswordError != null,
                        supportingText = {
                            if (oldPasswordError != null) {
                                Text(
                                    text = oldPasswordError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(stringResource(Res.string.auth_error_password_short_hint))
                            }
                        },
                        trailingIcon = {
                            val eyeText = if (oldPasswordVisible) "👁️" else "🙈"
                            Text(
                                text = eyeText,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .clickable { oldPasswordVisible = !oldPasswordVisible }
                                    .padding(4.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                testTag = "old_password_field"
                                contentDescription = "Champ ancien mot de passe"
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Nouveau mot de passe
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { viewModel.onNewPasswordChange(it) },
                        label = { Text(stringResource(Res.string.auth_new_password)) },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, 
                            imeAction = ImeAction.Next
                        ),
                        isError = newPasswordError != null,
                        supportingText = {
                            if (newPasswordError != null) {
                                Text(
                                    text = newPasswordError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(stringResource(Res.string.auth_error_password_short_hint))
                            }
                        },
                        trailingIcon = {
                            val eyeText = if (newPasswordVisible) "👁️" else "🙈"
                            Text(
                                text = eyeText,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .clickable { newPasswordVisible = !newPasswordVisible }
                                    .padding(4.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                testTag = "new_password_field"
                                contentDescription = "Champ nouveau mot de passe"
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirmer nouveau mot de passe
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { viewModel.onConfirmNewPasswordChange(it) },
                        label = { Text(stringResource(Res.string.auth_confirm_new_password)) },
                        visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, 
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            if (isChangePasswordFormValid) {
                                viewModel.changePassword()
                            }
                        }),
                        isError = confirmNewPasswordError != null,
                        supportingText = {
                            confirmNewPasswordError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            val eyeText = if (confirmNewPasswordVisible) "👁️" else "🙈"
                            Text(
                                text = eyeText,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .clickable { confirmNewPasswordVisible = !confirmNewPasswordVisible }
                                    .padding(4.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                testTag = "confirm_new_password_field"
                                contentDescription = "Champ confirmer nouveau mot de passe"
                            }
                    )

                    // Messages d'erreur/succès API
                    changePasswordApiError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    changePasswordSuccess?.let { success ->
                        Text(
                            text = success,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        // Fermer automatiquement après succès
                        LaunchedEffect(changePasswordSuccess) {
                            delay(2500)
                            showChangePasswordDialog = false
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.changePassword()
                    },
                    enabled = isChangePasswordFormValid && !isLoadingChangePassword
                ) {
                    if (isLoadingChangePassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.auth_confirm))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
        )
    }

    // Dialogue de sélection de langue (simplifié)
    if (showLanguageSelectionDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageSelectionDialog = false },
            title = { Text(stringResource(Res.string.language_selection_title)) },
            text = {
                Column {
                    Text(
                        text = stringResource(Res.string.language_settings_description),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Option Français
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentLanguage = "Français"
                                showLanguageSelectionDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == "Français") {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == "Français",
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(Res.string.language_french),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Option English
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentLanguage = "English"
                                showLanguageSelectionDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == "English") {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == "English",
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(Res.string.language_english),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageSelectionDialog = false }) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics {
                testTag = "info_row_${label.lowercase()}"
                contentDescription = "$label: $value"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.7f)
        )
    }
}