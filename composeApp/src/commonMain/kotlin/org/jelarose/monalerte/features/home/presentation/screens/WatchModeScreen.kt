package org.jelarose.monalerte.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Utilisation d'emojis pour une compatibilité cross-platform parfaite (iOS/Android)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jelarose.monalerte.core.utils.localizedString
import org.jelarose.monalerte.core.di.koinInject
import org.jelarose.monalerte.features.home.presentation.viewmodels.WatchModeViewModel
import org.jelarose.monalerte.features.home.presentation.viewmodels.WatchModeSecurityOption
import org.jelarose.monalerte.features.home.presentation.viewmodels.WatchModeUiEvent
import org.jelarose.monalerte.features.home.presentation.components.MediaPermissionDialog
import org.jelarose.monalerte.features.home.presentation.components.AudioRequiredDialog
import org.jelarose.monalerte.features.home.presentation.components.ErrorSnackbar
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect

/**
 * Écran du mode veille - Reproduction exacte de WatchMode2Activity.kt
 * Structure identique à l'original Android avec tous les composants
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchModeScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    println("🔧 WatchModeScreen: Composing WatchModeScreen")
    
    val watchModeViewModel: WatchModeViewModel = koinInject()
    val permissionsController: PermissionsController = koinInject()
    println("🔧 WatchModeScreen: ViewModel injected successfully")
    
    // Bind MOKO Permissions to current Activity
    BindEffect(permissionsController)
    
    val uiState by watchModeViewModel.uiState.collectAsStateWithLifecycle()
    val uiEvent by watchModeViewModel.uiEvents.collectAsStateWithLifecycle()
    println("🔧 WatchModeScreen: UI State: $uiState")
    val scrollState = rememberScrollState()
    
    // State pour les dialogues
    var showPermissionDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showAudioRequiredDialog by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf<String?>(null) }
    
    // Gérer les événements UI (permissions, erreurs)
    LaunchedEffect(uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is WatchModeUiEvent.ShowPermissionDialog -> {
                    println("🔐 WatchModeScreen: Permission dialog requested: ${event.permission}")
                    showPermissionDialog = event.permission to event.reason
                }
                is WatchModeUiEvent.ShowErrorMessage -> {
                    println("❌ WatchModeScreen: Error message: ${event.message}")
                    showErrorMessage = event.message
                }
                WatchModeUiEvent.AudioRequiredForVideo -> {
                    println("⚠️ WatchModeScreen: Audio required for video activation")
                    showAudioRequiredDialog = true
                }
            }
            watchModeViewModel.clearUiEvent()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = localizedString("watch_mode_title"),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Configuration Card (première section)
                ConfigurationCard()

                // Protection Features Section
                Text(
                    text = localizedString("protection_features_title"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Accident Detection Feature
                FeatureCard(
                    emoji = "⚠️",
                    title = localizedString("accident_detection_title_v2"),
                    description = localizedString("accident_detection_description_v2"),
                    isEnabled = false, // TODO: Connect to ViewModel state
                    onToggle = { /* TODO: viewModel.onSecurityOptionChanged(SecurityOption.ACCIDENT_DETECTION, !isEnabled) */ },
                    backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.error
                )

                // Shake Detection Feature
                FeatureCard(
                    emoji = "📳",
                    title = localizedString("shake_detection_title_v2"),
                    description = localizedString("shake_detection_description_v2"),
                    isEnabled = false, // TODO: Connect to ViewModel state
                    onToggle = { /* TODO: viewModel.onSecurityOptionChanged(SecurityOption.SHAKE_DETECTION, !isEnabled) */ },
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.tertiary
                )

                // Shortcuts Card
                ShortcutCard(
                    isEnabled = false, // TODO: Connect to settings
                    shortcutSequence = "", // TODO: Connect to settings
                    shortcutTimeout = 5000L, // TODO: Connect to settings
                    onToggle = { /* TODO: Handle toggle with accessibility service check */ },
                    onConfigureClick = { /* TODO: Navigate to ShortcutActivity */ }
                )

                // Zone Detection Card
                ZoneDetectionCard(
                    isEnabled = false, // TODO: Connect to ViewModel state
                    allPlaces = emptyList(), // TODO: Connect to places data
                    onToggle = { /* TODO: viewModel.toggleWatchModeGeofencing() */ }
                )

                // Location History Card
                LocationHistoryCard(
                    allPlaces = emptyList() // TODO: Connect to places data
                )

                // Alert Options Section
                Text(
                    text = localizedString("alert_options_title"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Audio Recording Feature
                FeatureCard(
                    emoji = "🎤",
                    title = localizedString("audio_recording_title_v2"),
                    description = localizedString("audio_recording_description_v2"),
                    isEnabled = uiState.isAudioRecordingEnabled,
                    onToggle = { 
                        println("🔊 WatchModeScreen: Audio toggle clicked")
                        watchModeViewModel.onSecurityOptionChanged(WatchModeSecurityOption.AUDIO_RECORDING, !uiState.isAudioRecordingEnabled)
                    },
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.primary
                )

                // Video Recording Feature avec gestion des dépendances
                FeatureCard(
                    emoji = "📹",
                    title = localizedString("video_recording_title_v2"),
                    description = if (!uiState.isVideoToggleEnabled) {
                        "L'audio doit être activé d'abord" // Message dynamique
                    } else {
                        localizedString("video_recording_description_v2")
                    },
                    isEnabled = uiState.isVideoRecordingEnabled,
                    isInteractable = uiState.isVideoToggleEnabled, // Nouveau paramètre pour contrôler l'interaction
                    onToggle = { 
                        println("📹 WatchModeScreen: Video toggle clicked")
                        watchModeViewModel.onSecurityOptionChanged(WatchModeSecurityOption.VIDEO_RECORDING, !uiState.isVideoRecordingEnabled)
                    },
                    backgroundColor = if (uiState.isVideoToggleEnabled) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f) // Apparence désactivée
                    },
                    iconTint = if (uiState.isVideoToggleEnabled) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) // Grisé
                    }
                )

                // Emergency Contacts Section
                Text(
                    text = localizedString("emergency_contacts_hardcoded"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                ContactsCard(
                    contactsCount = 0, // TODO: Connect to contacts data
                    onManageContacts = { /* TODO: Navigate to ContactActivity */ }
                )

                // SMS Management Section
                Text(
                    text = localizedString("sms_management_title"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                SmsConfigCard(
                    smsTemplate = uiState.smsTemplate,
                    onSmsTemplateChanged = { template -> 
                        println("💬 WatchModeScreen: SMS template changed")
                        watchModeViewModel.onSmsTemplateChanged(template) 
                    }
                )

                // Bottom spacing
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    // Dialogues de permission et d'erreur
    showPermissionDialog?.let { (permission, reason) ->
        MediaPermissionDialog(
            permission = permission,
            reason = reason,
            onConfirm = {
                println("🔐 WatchModeScreen: Permission dialog confirmed for: $permission")
                // Déclencher la VRAIE demande de permission système via MOKO
                watchModeViewModel.requestPermission(permission)
                showPermissionDialog = null
            },
            onDismiss = {
                println("🔐 WatchModeScreen: Permission dialog dismissed for: $permission")
                showPermissionDialog = null
            }
        )
    }
    
    if (showAudioRequiredDialog) {
        AudioRequiredDialog(
            onConfirm = {
                println("⚠️ WatchModeScreen: Audio required dialog confirmed")
                showAudioRequiredDialog = false
            },
            onDismiss = {
                println("⚠️ WatchModeScreen: Audio required dialog dismissed")
                showAudioRequiredDialog = false
            }
        )
    }
    
    showErrorMessage?.let { message ->
        ErrorSnackbar(
            message = message,
            onDismiss = {
                showErrorMessage = null
            }
        )
    }
}

@Composable
private fun ConfigurationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "configuration_card"
                contentDescription = "Configuration générale du mode veille"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = localizedString("watch_mode_configuration_title"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = localizedString("watch_mode_configuration_description"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FeatureCard(
    emoji: String? = null,
    customIcon: @Composable (() -> Unit)? = null,
    title: String,
    description: String,
    isEnabled: Boolean,
    isInteractable: Boolean = true, // Nouveau paramètre pour contrôler l'interaction
    onToggle: () -> Unit,
    backgroundColor: Color,
    iconTint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "feature_card_${title.lowercase().replace(" ", "_")}"
                contentDescription = "Fonctionnalité $title"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) backgroundColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isEnabled) iconTint.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (emoji != null) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isEnabled) iconTint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                } else {
                    customIcon?.invoke()
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isEnabled,
                enabled = isInteractable, // Contrôle si le switch est cliquable
                onCheckedChange = { if (isInteractable) onToggle() }, // Seulement si interactable
                modifier = Modifier.semantics {
                    testTag = "feature_switch_${title.lowercase().replace(" ", "_")}"
                    contentDescription = if (isInteractable) {
                        "Activer/Désactiver $title"
                    } else {
                        "$title - Nécessite l'activation de l'audio d'abord"
                    }
                }
            )
        }
    }
}

@Composable
private fun ZoneDetectionCard(
    isEnabled: Boolean,
    allPlaces: List<Any>, // TODO: Replace with proper SavedPlace type
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "zone_detection_card"
                contentDescription = "Détection de sortie de zone"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled && allPlaces.isNotEmpty())
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isEnabled)
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📍",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isEnabled)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localizedString("zone_detection_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = localizedString("zone_detection_description"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                    )
                )
            }

            if (isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))

                if (allPlaces.isNotEmpty()) {
                    // TODO: Implement places list with checkboxes like original
                    Text(
                        text = localizedString("zone_places_count").replace("%s", allPlaces.size.toString()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = localizedString("no_zones_configured"),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = localizedString("configure_zones_description"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShortcutCard(
    isEnabled: Boolean,
    shortcutSequence: String,
    shortcutTimeout: Long,
    onToggle: (Boolean) -> Unit,
    onConfigureClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "shortcut_card"
                contentDescription = "Raccourcis d'urgence"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isEnabled)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔘",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localizedString("emergency_shortcuts_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = localizedString("emergency_shortcuts_description"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            if (isEnabled && shortcutSequence.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfigureClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(localizedString("configure_shortcuts_button"))
                }
            }
        }
    }
}

@Composable
private fun LocationHistoryCard(
    allPlaces: List<Any> // TODO: Replace with proper SavedPlace type
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "location_history_card"
                contentDescription = "Historique de localisation"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📍",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = localizedString("location_history_title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = localizedString("location_history_description"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { /* TODO: Navigate to location history */ }
            ) {
                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ContactsCard(
    contactsCount: Int,
    onManageContacts: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "contacts_card"
                contentDescription = "Gestion des contacts d'urgence"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onManageContacts() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👥",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = localizedString("emergency_contacts_title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (contactsCount > 0) {
                        localizedString("contacts_count").replace("%s", contactsCount.toString())
                    } else {
                        localizedString("no_contacts_configured")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "→",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SmsConfigCard(
    smsTemplate: String,
    onSmsTemplateChanged: (String) -> Unit
) {
    var showSmsEditor by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                testTag = "sms_config_card"
                contentDescription = "Configuration des messages SMS"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💬",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localizedString("sms_configuration_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (smsTemplate.isNotEmpty()) 
                            localizedString("sms_configuration_custom")
                        else 
                            localizedString("sms_configuration_default"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                TextButton(
                    onClick = { showSmsEditor = true },
                    modifier = Modifier.semantics {
                        testTag = "sms_config_edit_button"
                        contentDescription = "Modifier le modèle SMS"
                    }
                ) {
                    Text(
                        text = localizedString("edit_button"),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Show current template preview if exists
            if (smsTemplate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = smsTemplate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                        maxLines = 3
                    )
                }
            }
        }
    }
    
    // SMS Editor Dialog
    if (showSmsEditor) {
        SmsTemplateEditorDialog(
            currentTemplate = smsTemplate,
            onTemplateChanged = onSmsTemplateChanged,
            onDismiss = { showSmsEditor = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmsTemplateEditorDialog(
    currentTemplate: String,
    onTemplateChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var editedTemplate by remember { mutableStateOf(currentTemplate) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = localizedString("sms_template_editor_title"),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = localizedString("sms_template_editor_description"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = editedTemplate,
                    onValueChange = { editedTemplate = it },
                    label = { Text(localizedString("sms_template_label")) },
                    placeholder = { Text(localizedString("sms_template_placeholder")) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = localizedString("sms_template_hint"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    println("💬 WatchModeScreen: SMS template updated")
                    onTemplateChanged(editedTemplate)
                    onDismiss()
                }
            ) {
                Text(localizedString("save_button"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedString("cancel_button"))
            }
        }
    )
}