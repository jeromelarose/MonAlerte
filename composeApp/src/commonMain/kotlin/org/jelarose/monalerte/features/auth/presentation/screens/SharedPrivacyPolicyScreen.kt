package org.jelarose.monalerte.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jelarose.monalerte.core.utils.localizedString
import org.jelarose.monalerte.core.policy.PolicyManager
import org.jelarose.monalerte.core.components.WebViewComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedPrivacyPolicyScreen(
    policyManager: PolicyManager,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val requiredPolicyVersion = PolicyManager.CURRENT_POLICY_VERSION
    val privacyPolicyUrl = "https://51.75.120.88.nip.io/politique-confidentialite/$requiredPolicyVersion"
    
    // Debug logging pour vérifier l'URL des politiques
    println("SharedPrivacyPolicyScreen - Policy Version: $requiredPolicyVersion")
    println("SharedPrivacyPolicyScreen - Policy URL: $privacyPolicyUrl")

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDecline) {
                        Text(localizedString("common_cancel"))
                    }
                    Button(onClick = onAccept) {
                        Text(localizedString("privacy_policy_accept_and_continue"))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                "Mise à jour de la Politique de Confidentialité",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                "Veuillez lire et accepter notre politique de confidentialité mise à jour pour continuer à utiliser l'application.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))

            // WebView to display the policy from the URL
            WebViewComponent(
                url = privacyPolicyUrl,
                modifier = Modifier.weight(1f)
            )
        }
    }
}