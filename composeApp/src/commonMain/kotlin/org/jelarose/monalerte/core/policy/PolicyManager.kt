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
            _policyState.value = PolicyState.Loading
            
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
            _policyState.value = PolicyState.Accepted
            logger.i { "Policy accepted successfully" }
        } catch (e: Exception) {
            logger.e(e) { "Error accepting policy" }
        }
    }
    
    /**
     * Get currently accepted policy version
     */
    private suspend fun getAcceptedPolicyVersion(): Int {
        return try {
            sharedDataStore.getInt(POLICY_VERSION_KEY) ?: 0
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
}