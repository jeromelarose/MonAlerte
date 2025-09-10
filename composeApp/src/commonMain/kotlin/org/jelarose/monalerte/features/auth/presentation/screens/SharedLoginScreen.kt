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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.features.auth.presentation.viewmodels.SharedAuthViewModel
import org.jelarose.monalerte.features.auth.presentation.components.LanguageSelector
import org.jelarose.monalerte.core.utils.localizedString
import monalerte.composeapp.generated.resources.Res
import monalerte.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedLoginScreen(
    viewModel: SharedAuthViewModel,
    onLoginSuccess: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val customErrorMessage by viewModel.customErrorMessage.collectAsState()
    val showDialog by viewModel.showRegistrationSuccessDialog.collectAsState()
    val registrationSuccessMessage by viewModel.registrationSuccessMessage.collectAsState()

    var localEmailErrorResId by remember { mutableStateOf<String?>(null) }
    var localPasswordErrorResId by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Validation effects
    LaunchedEffect(email) {
        localEmailErrorResId = if (email.isBlank()) null else viewModel.validateEmail(email)
    }

    LaunchedEffect(password) {
        localPasswordErrorResId = if (password.isBlank()) null else viewModel.validatePassword(password)
    }

    // Registration success dialog
    registrationSuccessMessage?.let { message ->
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissRegistrationSuccessDialog() },
                title = { Text(localizedString("registration_success_dialog_title")) },
                text = { Text(message) },
                confirmButton = {
                    Button(onClick = { viewModel.dismissRegistrationSuccessDialog() }) {
                        Text(localizedString("registration_success_dialog_ok"))
                    }
                }
            )
        }
    }

    // Form validation
    val isEmailValid = email.isNotBlank() && localEmailErrorResId == null
    val isPasswordValid = password.isNotBlank() && localPasswordErrorResId == null
    val isFormValid = isEmailValid && isPasswordValid

    // Handle login success
    val authState by viewModel.authState.collectAsState()
    LaunchedEffect(authState) {
        if (authState is org.jelarose.monalerte.features.auth.presentation.viewmodels.AuthState.Success) {
            onLoginSuccess()
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
        // Language selector at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            LanguageSelector(
                onLanguageChanged = { languageCode ->
                    // Language changed - app will need to be restarted for full effect
                    // For now, just log the change
                    co.touchlab.kermit.Logger.d("SharedLoginScreen") { "Language changed to: $languageCode" }
                }
            )
        }
        
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
            text = localizedString("auth_welcome_back"),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = localizedString("auth_title_login"),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { 
                viewModel.updatePassword(it)
            },
            label = { Text(localizedString("auth_label_password")) },
            isError = localPasswordErrorResId != null && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                val description = if (passwordVisible) {
                    localizedString("auth_content_desc_hide_password")
                } else {
                    localizedString("auth_content_desc_show_password")
                }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = if (passwordVisible) {
                            painterResource(Res.drawable.ic_visibility_off)
                        } else {
                            painterResource(Res.drawable.ic_visibility)
                        },
                        contentDescription = description
                    )
                }
            }
        )

        // Password error message
        if (localPasswordErrorResId != null && password.isNotBlank()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = when (localPasswordErrorResId) {
                        "auth_error_password_empty" -> localizedString("auth_error_password_empty")
                        "auth_error_password_short" -> localizedString("auth_error_password_short")
                        else -> localizedString("auth_error_password_short_hint")
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot password link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPassword
            ) {
                Text(localizedString("auth_link_forgot_password"))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    "auth_error_password_empty" -> localizedString("auth_error_password_empty")
                    "auth_error_password_short" -> localizedString("auth_error_password_short")
                    else -> message
                },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Login button
        Button(
            onClick = {
                viewModel.login()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(localizedString("auth_button_connect"))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register link
        TextButton(
            onClick = onSwitchToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(localizedString("auth_link_no_account_register"))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}