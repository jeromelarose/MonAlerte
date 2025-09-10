package org.jelarose.monalerte.features.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.core.utils.localizedString
import monalerte.composeapp.generated.resources.Res
import monalerte.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedForgotPasswordScreen(
    viewModel: SharedAuthViewModel,
    onBack: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val customErrorMessage by viewModel.customErrorMessage.collectAsState()
    val registrationSuccessMessage by viewModel.registrationSuccessMessage.collectAsState()

    var localEmailErrorResId by remember { mutableStateOf<String?>(null) }
    var resetEmailSent by remember { mutableStateOf(false) }

    // Validation effect
    LaunchedEffect(email) {
        localEmailErrorResId = if (email.isBlank()) null else viewModel.validateEmail(email)
    }

    // Form validation
    val isEmailValid = email.isNotBlank() && localEmailErrorResId == null

    // Handle success message
    LaunchedEffect(registrationSuccessMessage) {
        if (registrationSuccessMessage != null) {
            resetEmailSent = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = localizedString("auth_logo_description_content"),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Welcome message
        Text(
            text = localizedString("auth_welcome_forgot_password"),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = localizedString("auth_title_reset_password"),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (resetEmailSent) {
            // Success state
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = registrationSuccessMessage ?: localizedString("auth_api_forgot_password_success"),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Back to login button
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(localizedString("auth_button_return"))
            }

        } else {
            // Form state
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    viewModel.updateEmail(it)
                },
                label = { Text(localizedString("auth_label_email")) },
                isError = localEmailErrorResId != null && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            // Email error message
            if (localEmailErrorResId != null && email.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = when (localEmailErrorResId) {
                            "auth_error_email_empty" -> localizedString("auth_error_email_empty")
                            "auth_error_email_invalid_format" -> localizedString("auth_error_email_invalid_format")
                            else -> localEmailErrorResId ?: ""
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            customErrorMessage?.let { message ->
                Text(
                    text = when (message) {
                        "auth_error_email_empty" -> localizedString("auth_error_email_empty")
                        "auth_error_email_invalid_format" -> localizedString("auth_error_email_invalid_format")
                        else -> message
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Send reset link button
            Button(
                onClick = {
                    viewModel.forgotPassword()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEmailValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(localizedString("auth_button_send"))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back to login link
            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(localizedString("auth_button_return"))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}