package org.jelarose.monalerte.features.contacts.presentation.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jelarose.monalerte.features.contacts.presentation.viewmodel.ContactListViewModel
import org.jelarose.monalerte.core.di.koinInject

data class ContactScreen(
    val contactType: String = "PARAMETER",
    val initialListId: String? = null
) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactListViewModel = koinInject()
        
        ContactScreenContent(
            viewModel = viewModel,
            type = contactType,
            initialListId = initialListId,
            onBack = {
                navigator.pop()
            }
        )
    }
}