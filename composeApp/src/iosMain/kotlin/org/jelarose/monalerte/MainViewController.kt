package org.jelarose.monalerte

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { 
    // Initialize Koin before creating the Compose UI
    ensureKoinInitialized()
    App() 
}