package org.jelarose.monalerte.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String
)