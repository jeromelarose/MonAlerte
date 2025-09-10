package org.jelarose.monalerte.core.policy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jelarose.monalerte.core.utils.SharedDataStore
import co.touchlab.kermit.Logger

/**
 * Manager for handling privacy policy acceptance and versioning
 */
class PolicyManager(
    private val sharedDataStore: SharedDataStore
) {
    private val logger = Logger.withTag("PolicyManager")
    
    companion object {
        const val CURRENT_POLICY_VERSION = 1
        private const val POLICY_VERSION_KEY = "accepted_policy_version"
    }
    
    private val _policyState = MutableStateFlow<PolicyState>(PolicyState.Loading)
    val policyState: StateFlow<PolicyState> = _policyState.asStateFlow()
    
    /**
     * Check current policy status
     */
    suspend fun checkPolicyStatus() {
        try {
            logger.d { "Checking policy status..." }
            
            // Only set to Loading if not already Accepted to avoid state regression
            if (_policyState.value !is PolicyState.Accepted) {
                _policyState.value = PolicyState.Loading
            }
            
            val acceptedVersion = getAcceptedPolicyVersion()
            logger.d { "Current accepted version: $acceptedVersion, required: $CURRENT_POLICY_VERSION" }
            
            if (acceptedVersion >= CURRENT_POLICY_VERSION) {
                _policyState.value = PolicyState.Accepted
                logger.i { "Policy already accepted (version $acceptedVersion)" }
            } else {
                _policyState.value = PolicyState.Required(CURRENT_POLICY_VERSION)
                logger.i { "Policy acceptance required (version $CURRENT_POLICY_VERSION)" }
            }
        } catch (e: Exception) {
            logger.e(e) { "Error checking policy status" }
            _policyState.value = PolicyState.Required(CURRENT_POLICY_VERSION)
        }
    }
    
    /**
     * Mark policy as accepted
     */
    suspend fun acceptPolicy() {
        try {
            logger.i { "Accepting policy version $CURRENT_POLICY_VERSION" }
            sharedDataStore.putInt(POLICY_VERSION_KEY, CURRENT_POLICY_VERSION)
            
            // Vérification immédiate pour debugging iOS
            val savedVersion = sharedDataStore.getInt(POLICY_VERSION_KEY, defaultValue = 0)
            logger.i { "Policy saved and immediately verified: saved=$savedVersion, expected=$CURRENT_POLICY_VERSION" }
            
            if (savedVersion == CURRENT_POLICY_VERSION) {
                _policyState.value = PolicyState.Accepted
                logger.i { "Policy accepted successfully and verified in storage" }
            } else {
                logger.w { "Policy save verification failed: saved=$savedVersion vs expected=$CURRENT_POLICY_VERSION" }
                _policyState.value = PolicyState.Accepted // Still accept to avoid blocking user
            }
        } catch (e: Exception) {
            logger.e(e) { "Error accepting policy" }
        }
    }
    
    /**
     * Get currently accepted policy version
     */
    private suspend fun getAcceptedPolicyVersion(): Int {
        return try {
            val version = sharedDataStore.getInt(POLICY_VERSION_KEY, defaultValue = 0)
            logger.d { "Retrieved policy version from storage: $version" }
            version
        } catch (e: Exception) {
            logger.e(e) { "Error getting accepted policy version" }
            0
        }
    }
    
    /**
     * Check if policy has been accepted
     */
    suspend fun isPolicyAccepted(): Boolean {
        return getAcceptedPolicyVersion() >= CURRENT_POLICY_VERSION
    }
    
    /**
     * Get the version for API requests
     */
    suspend fun getAcceptedPolicyVersionForApi(): Int {
        return if (isPolicyAccepted()) CURRENT_POLICY_VERSION else 0
    }
    
    /**
     * Debug method to force check and log policy status
     * Useful for iOS debugging
     */
    suspend fun debugPolicyStatus(): String {
        return try {
            val storedVersion = getAcceptedPolicyVersion()
            val requiredVersion = CURRENT_POLICY_VERSION
            val isAccepted = storedVersion >= requiredVersion
            val debugInfo = """
                |Policy Debug Status:
                |  Stored version: $storedVersion
                |  Required version: $requiredVersion
                |  Is accepted: $isAccepted
                |  Current state: ${_policyState.value}
                |  Storage key: $POLICY_VERSION_KEY
            """.trimMargin()
            
            logger.i { debugInfo }
            debugInfo
        } catch (e: Exception) {
            val errorInfo = "Policy debug error: ${e.message}"
            logger.e(e) { errorInfo }
            errorInfo
        }
    }
}