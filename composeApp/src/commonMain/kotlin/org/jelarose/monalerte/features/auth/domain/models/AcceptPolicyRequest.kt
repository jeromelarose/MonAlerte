package org.jelarose.monalerte.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AcceptPolicyRequest(
    val policyVersion: Int
)