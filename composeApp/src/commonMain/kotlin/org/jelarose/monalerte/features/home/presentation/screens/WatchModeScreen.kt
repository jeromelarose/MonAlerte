package org.jelarose.monalerte.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
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
import org.jelarose.monalerte.core.utils.localizedString

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
    val scrollState = rememberScrollState()
    
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = localizedString("back_button_desc")
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
                    icon = Icons.Filled.Warning,
                    title = localizedString("accident_detection_title_v2"),
                    description = localizedString("accident_detection_description_v2"),
                    isEnabled = false, // TODO: Connect to ViewModel state
                    onToggle = { /* TODO: viewModel.onSecurityOptionChanged(SecurityOption.ACCIDENT_DETECTION, !isEnabled) */ },
                    backgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.error
                )

                // Shake Detection Feature
                FeatureCard(
                    icon = null,
                    customIcon = {
                        // TODO: Use shake drawable icon from resources
                        Text(
                            text = "📳",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    },
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
                    icon = null,
                    customIcon = {
                        Text(
                            text = "🎤",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    title = localizedString("audio_recording_title_v2"),
                    description = localizedString("audio_recording_description_v2"),
                    isEnabled = false, // TODO: Connect to ViewModel state
                    onToggle = { /* TODO: viewModel.onSecurityOptionChanged(SecurityOption.MANUAL_AUDIO, !isEnabled) */ },
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.primary
                )

                // Video Recording Feature
                FeatureCard(
                    icon = null,
                    customIcon = {
                        Text(
                            text = "📹",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    title = localizedString("video_recording_title_v2"),
                    description = localizedString("video_recording_description_v2"),
                    isEnabled = false, // TODO: Connect to ViewModel state
                    onToggle = { /* TODO: viewModel.onSecurityOptionChanged(SecurityOption.MANUAL_VIDEO, !isEnabled) */ },
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    iconTint = MaterialTheme.colorScheme.secondary
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
                    // TODO: Connect to settings
                )

                // Bottom spacing
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
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
    icon: ImageVector?,
    customIcon: @Composable (() -> Unit)? = null,
    title: String,
    description: String,
    isEnabled: Boolean,
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
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isEnabled) iconTint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
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
                onCheckedChange = { onToggle() },
                modifier = Modifier.semantics {
                    testTag = "feature_switch_${title.lowercase().replace(" ", "_")}"
                    contentDescription = "Activer/Désactiver $title"
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
private fun SmsConfigCard() {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Navigate to SMS configuration */ }
                .padding(16.dp),
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
                    text = localizedString("sms_configuration_description"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
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