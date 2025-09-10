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

    if (hasError) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erreur lors du chargement de la politique",
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        UIKitView(
            modifier = modifier,
            factory = {
                WKWebView().apply {
                    navigationDelegate = object : NSObject(), WKNavigationDelegateProtocol {
                        override fun webView(
                            webView: WKWebView, 
                            didFinishNavigation: WKNavigation?
                        ) {
                            isLoading = false
                        }
                        
                        override fun webView(
                            webView: WKWebView, 
                            didFailNavigation: WKNavigation?, 
                            withError: platform.Foundation.NSError
                        ) {
                            isLoading = false
                            hasError = true
                        }
                    }
                    
                    val nsUrl = NSURL.URLWithString(url)
                    if (nsUrl != null) {
                        val request = NSURLRequest.requestWithURL(nsUrl)
                        loadRequest(request)
                    } else {
                        hasError = true
                    }
                }
            }
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}