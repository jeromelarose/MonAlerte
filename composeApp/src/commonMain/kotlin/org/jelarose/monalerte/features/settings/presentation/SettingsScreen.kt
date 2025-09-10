package org.jelarose.monalerte.features.settings.presentation

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import monalerte.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Écran des paramètres - réplique exacte de SettingsMainScreen
 * Affiche 8 options de paramètres avec navigation vers différentes sous-pages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onEmergencyContactsClick: () -> Unit = {},
    onShortcutsClick: () -> Unit = {},
    onPermissionsClick: () -> Unit = {},
    onSmsAlertClick: () -> Unit = {},
    onWidgetConfigClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.semantics {
            testTag = "settings_scaffold"
            contentDescription = "Écran paramètres principal"
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(Res.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.semantics {
                            testTag = "settings_title"
                            contentDescription = "Titre Paramètres"
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.semantics {
                            testTag = "settings_back_button"
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
                    testTag = "settings_topbar"
                    contentDescription = "Barre de Titre Paramètres"
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
            // Définir les items de settings dans le contexte @Composable
            val settingsItems = listOf(
                SettingItem(
                    title = stringResource(Res.string.settings_account_title),
                    description = stringResource(Res.string.settings_account_description),
                    emoji = "👤",
                    testTag = "settings_account"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_emergency_contacts_title),
                    description = stringResource(Res.string.settings_emergency_contacts_description),
                    emoji = "📞",
                    testTag = "settings_emergency_contacts"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_shortcuts_title),
                    description = stringResource(Res.string.settings_shortcuts_description),
                    emoji = "⚡",
                    testTag = "settings_shortcuts"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_permissions_title),
                    description = stringResource(Res.string.settings_permissions_description),
                    emoji = "🔐",
                    testTag = "settings_permissions"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_sms_alert_title),
                    description = stringResource(Res.string.settings_sms_alert_description),
                    emoji = "💬",
                    testTag = "settings_sms_alert"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_widget_config_title),
                    description = stringResource(Res.string.settings_widget_config_description),
                    emoji = "🧩",
                    testTag = "settings_widget_config"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_notifications_title),
                    description = stringResource(Res.string.settings_notifications_description),
                    emoji = "🔔",
                    testTag = "settings_notifications"
                ),
                SettingItem(
                    title = stringResource(Res.string.settings_privacy_policy_title),
                    description = stringResource(Res.string.settings_privacy_policy_description),
                    emoji = "📋",
                    testTag = "settings_privacy_policy"
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 8.dp)
                    .semantics {
                        testTag = "settings_content"
                        contentDescription = "Liste des paramètres"
                    }
            ) {
                itemsIndexed(settingsItems) { index, item ->
                    SettingListItem(
                        item = item,
                        onClick = {
                            when (index) {
                                0 -> onAccountClick()
                                1 -> onEmergencyContactsClick()
                                2 -> onShortcutsClick()
                                3 -> onPermissionsClick()
                                4 -> onSmsAlertClick()
                                5 -> onWidgetConfigClick()
                                6 -> onNotificationsClick()
                                7 -> onPrivacyPolicyClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Data class pour représenter un item de paramètres
 */
data class SettingItem(
    val title: String,
    val description: String,
    val emoji: String,
    val testTag: String
)


@Composable
private fun SettingListItem(
    item: SettingItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .semantics {
                testTag = item.testTag
                contentDescription = "${item.title} - ${item.description}"
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji dans un cercle avec fond coloré
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics {
                        testTag = "${item.testTag}_icon"
                        contentDescription = "Icône ${item.title}"
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Contenu texte
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics {
                        testTag = "${item.testTag}_title"
                        contentDescription = "Titre: ${item.title}"
                    }
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics {
                        testTag = "${item.testTag}_description"
                        contentDescription = "Description: ${item.description}"
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Flèche indicatrice
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.semantics {
                    contentDescription = "Naviguer vers ${item.title}"
                }
            )
        }
    }
}