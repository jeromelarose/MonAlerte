package org.jelarose.monalerte.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Utilisez du texte emoji pour une compatibilité parfaite cross-platform
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.core.utils.localizedString

/**
 * Écran du menu des interfaces - réplique exacte de InterfaceMenuActivity
 * Affiche 5 cartes pour naviguer vers différentes parties de l'application
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterfaceMenuScreen(
    onSuiviPositionClick: () -> Unit = {},
    onVeilleClick: () -> Unit = {},
    onVeille2Click: () -> Unit = {},
    onAddressSelectionClick: () -> Unit = {},
    onLocationHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val interfaceMenuContentDesc = localizedString("interface_menu_content_desc")
    
    Scaffold(
        modifier = Modifier.semantics {
            testTag = "interface_menu_scaffold"
            contentDescription = interfaceMenuContentDesc
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = localizedString("interface_menu_title"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.semantics {
                            testTag = "interface_menu_title"
                            contentDescription = "Titre Menu des Interfaces"
                        }
                    ) 
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.semantics {
                            testTag = "settings_button"
                            contentDescription = "Bouton Paramètres"
                        }
                    ) {
                        Text(
                            text = "⚙️",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.semantics {
                    testTag = "interface_menu_topbar"
                    contentDescription = "Barre de Titre Menu Interfaces"
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
                    .semantics {
                        testTag = "interface_menu_content"
                        contentDescription = "Contenu Principal Menu Interfaces"
                    },
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // En-tête avec icône et description
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "📱",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = localizedString("interface_menu_description"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .semantics {
                                testTag = "interface_menu_description"
                                contentDescription = "Description Choix du Mode"
                            }
                    )
                }
                
                // Ordre: 1. Veille (ex-Veille Plus), 2. Position/itinéraire, 3. Gérer les lieux, 4. Ancien Veille
                ModernInterfaceCard(
                    title = localizedString("interface_menu_watch_mode_title"),
                    description = localizedString("interface_menu_watch_mode_2_description"),
                    emoji = "👁️",
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    ),
                    onClick = onVeille2Click,
                    testTag = "watch_mode_card"
                )
                
                ModernInterfaceCard(
                    title = localizedString("interface_menu_position_tracking_title"),
                    description = localizedString("interface_menu_position_tracking_description"),
                    emoji = "🧭",
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    ),
                    onClick = onSuiviPositionClick,
                    testTag = "position_tracking_card"
                )
                
                ModernInterfaceCard(
                    title = localizedString("interface_menu_address_selection_title"),
                    description = localizedString("interface_menu_address_selection_description"),
                    emoji = "🏠",
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                    ),
                    onClick = onAddressSelectionClick,
                    testTag = "address_selection_card"
                )
                
                ModernInterfaceCard(
                    title = localizedString("interface_menu_location_history_title"),
                    description = localizedString("interface_menu_location_history_description"),
                    emoji = "📍",
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.surface.copy(red = 0.6f, green = 0.4f, blue = 0.9f),
                        MaterialTheme.colorScheme.surface.copy(red = 0.6f, green = 0.4f, blue = 0.9f, alpha = 0.7f)
                    ),
                    onClick = onLocationHistoryClick,
                    testTag = "location_history_card"
                )
                
                ModernInterfaceCard(
                    title = localizedString("interface_menu_old_watch_mode_title"),
                    description = localizedString("mode_standby_description"),
                    emoji = "⚙️",
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                    ),
                    onClick = onVeilleClick,
                    testTag = "old_watch_mode_card"
                )
            }
        }
    }
}

@Composable
private fun ModernInterfaceCard(
    title: String,
    description: String,
    emoji: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .semantics {
                this.testTag = testTag
                contentDescription = "Carte $title - $description"
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Emoji dans un cercle avec ombre
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.semantics {
                            this.testTag = "${testTag}_icon"
                            this.contentDescription = "Icône $title"
                        }
                    )
                }
                
                // Contenu texte
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.semantics {
                            this.testTag = "${testTag}_title"
                            this.contentDescription = "Titre: $title"
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                        modifier = Modifier.semantics {
                            this.testTag = "${testTag}_description"
                            this.contentDescription = "Description: $description"
                        }
                    )
                }
                
                // Flèche indicatrice
                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}