package org.jelarose.monalerte.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val acceptedPolicyVersion: Int? = null
    // Note: firstName, lastName, phoneNumber excluded to match original Android app
    // The original app's bridge only sent email, password, and acceptedPolicyVersion
)