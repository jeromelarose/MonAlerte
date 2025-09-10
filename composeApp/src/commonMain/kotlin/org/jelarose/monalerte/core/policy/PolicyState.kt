package org.jelarose.monalerte.core.policy

/**
 * State representing the privacy policy acceptance status
 */
sealed class PolicyState {
    object Loading : PolicyState()
    object Accepted : PolicyState()
    data class Required(val version: Int) : PolicyState()
}