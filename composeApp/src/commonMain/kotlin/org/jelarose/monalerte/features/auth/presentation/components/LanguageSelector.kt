package org.jelarose.monalerte.features.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jelarose.monalerte.core.di.koinInject
import org.jelarose.monalerte.core.utils.LanguageManager
import org.jelarose.monalerte.core.utils.localizedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    onLanguageChanged: (String) -> Unit = {}
) {
    val languageManager: LanguageManager = koinInject()
    val coroutineScope = rememberCoroutineScope()
    
    val currentLanguage by languageManager.currentLanguage.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Language selector button
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { showLanguageDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🌐",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = languageManager.getLanguageDisplayName(currentLanguage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Language selection dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(localizedString("language_selection_title")) },
            text = {
                Column {
                    LanguageOption(
                        language = LanguageManager.LANGUAGE_FRENCH,
                        displayName = localizedString("language_french"),
                        isSelected = currentLanguage == LanguageManager.LANGUAGE_FRENCH,
                        onClick = {
                            coroutineScope.launch {
                                changeLanguage(
                                    languageManager = languageManager,
                                    languageCode = LanguageManager.LANGUAGE_FRENCH,
                                    onLanguageChanged = onLanguageChanged,
                                    onDialogDismiss = { showLanguageDialog = false }
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LanguageOption(
                        language = LanguageManager.LANGUAGE_ENGLISH,
                        displayName = localizedString("language_english"),
                        isSelected = currentLanguage == LanguageManager.LANGUAGE_ENGLISH,
                        onClick = {
                            coroutineScope.launch {
                                changeLanguage(
                                    languageManager = languageManager,
                                    languageCode = LanguageManager.LANGUAGE_ENGLISH,
                                    onLanguageChanged = onLanguageChanged,
                                    onDialogDismiss = { showLanguageDialog = false }
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
private fun LanguageOption(
    language: String,
    displayName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.primary
                )
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

private suspend fun changeLanguage(
    languageManager: LanguageManager,
    languageCode: String,
    onLanguageChanged: (String) -> Unit,
    onDialogDismiss: () -> Unit
) {
    try {
        co.touchlab.kermit.Logger.d("LanguageSelector") { "=== Starting Language Change Process ===" }
        co.touchlab.kermit.Logger.d("LanguageSelector") { "Changing language to: $languageCode" }
        
        // Close dialog first
        onDialogDismiss()
        
        // Set the new language preference
        languageManager.setLanguage(languageCode)
        co.touchlab.kermit.Logger.d("LanguageSelector") { "Language preference saved" }
        
        // Small delay to allow any pending state saves to complete
        kotlinx.coroutines.delay(150)
        co.touchlab.kermit.Logger.d("LanguageSelector") { "State save delay completed" }
        
        // Notify that language has changed
        onLanguageChanged(languageCode)
        co.touchlab.kermit.Logger.d("LanguageSelector") { "Language change notification sent" }
        
    } catch (e: Exception) {
        co.touchlab.kermit.Logger.e("LanguageSelector", e) { "Error during language change" }
        onDialogDismiss()
    }
}