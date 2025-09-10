package org.jelarose.monalerte.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigation
import platform.darwin.NSObject

/**
 * iOS implementation of WebView component using WKWebView
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebViewComponent(
    url: String,
    modifier: Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Le WebView est toujours présent
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WKWebView().apply {
                    navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                        override fun webView(
                            webView: WKWebView, 
                            didFinishNavigation: WKNavigation?
                        ) {
                            println("WebView successfully loaded: $url")
                            isLoading = false
                        }
                        
                        override fun webView(
                            webView: WKWebView, 
                            didFailNavigation: WKNavigation?, 
                            withError: platform.Foundation.NSError
                        ) {
                            println("WebView failed to load: ${withError.localizedDescription}")
                            println("Error code: ${withError.code}")
                            println("Error domain: ${withError.domain}")
                            isLoading = false
                            hasError = true
                        }
                    }
                    
                    // Validate and load URL with detailed logging
                    val trimmedUrl = url.trim()
                    println("WebView attempting to load URL: $trimmedUrl")
                    
                    val nsUrl = NSURL.URLWithString(trimmedUrl)
                    if (nsUrl != null) {
                        println("URL validated successfully: ${nsUrl.absoluteString}")
                        val request = NSURLRequest.requestWithURL(nsUrl)
                        loadRequest(request)
                    } else {
                        println("ERROR: Invalid URL format: $trimmedUrl")
                        hasError = true
                        isLoading = false
                    }
                }
            }
        )
        
        // Overlay pour l'état de chargement ou d'erreur
        when {
            hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Erreur lors du chargement de la politique",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}