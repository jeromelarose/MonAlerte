package org.jelarose.monalerte.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-platform WebView component for loading web content
 */
@Composable
expect fun WebViewComponent(
    url: String,
    modifier: Modifier = Modifier
)