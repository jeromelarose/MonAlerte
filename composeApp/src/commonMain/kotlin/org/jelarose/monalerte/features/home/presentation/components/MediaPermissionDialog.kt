package org.jelarose.monalerte.features.home.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.core.utils.localizedString
import org.jelarose.monalerte.features.home.domain.WatchModeMediaLogic

/**
 * Dialogue de demande de permission média
 * Reproduit le comportement du PermissionRequestDialog natif
 */
@Composable
fun MediaPermissionDialog(
    permission: String,
    reason: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val permissionType = when (permission) {
        WatchModeMediaLogic.AUDIO_PERMISSION -> "microphone"
        WatchModeMediaLogic.CAMERA_PERMISSION -> "caméra"
        else -> "permission"
    }
    
    val title = when (permission) {
        WatchModeMediaLogic.AUDIO_PERMISSION -> "Permission Microphone"
        WatchModeMediaLogic.CAMERA_PERMISSION -> "Permission Caméra"
        else -> "Permission Requise"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Cette fonctionnalité nécessite l'accès au $permissionType pour fonctionner correctement.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Autoriser")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}

/**
 * Dialogue d'erreur pour la dépendance audio/vidéo
 * Équivalent au toast natif "Audio required for video"
 */
@Composable
fun AudioRequiredDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Audio Requis",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "L'enregistrement vidéo nécessite que l'enregistrement audio soit activé en premier. Veuillez d'abord activer l'audio.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Compris")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}

/**
 * Snackbar simple pour les messages d'erreur
 */
@Composable
fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        // Auto-dismiss after 3 seconds
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }
    
    // Simple text display for now - could be enhanced with SnackbarHost
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}