package org.jelarose.monalerte.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val acceptedPolicyVersion: Int? = null
)