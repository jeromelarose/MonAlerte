package org.jelarose.monalerte.features.contacts.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // On iOS, back handling is typically managed by the navigation controller
    // This is a simplified implementation - in production, you might want to
    // integrate with the iOS navigation system
    DisposableEffect(enabled) {
        onDispose { }
    }
}