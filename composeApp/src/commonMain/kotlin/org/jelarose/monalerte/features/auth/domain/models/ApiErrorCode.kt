package org.jelarose.monalerte.features.auth.domain.models

enum class ApiErrorCode {
    EMAIL_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    EMAIL_NOT_FOUND,
    INTERNAL_SERVER_ERROR,
    UNKNOWN_ROUTE,
    MALFORMED_JSON_REQUEST,
    MISSING_TOKEN_IN_RESPONSE,
    EMAIL_NOT_VERIFIED,
    MISSING_FIELDS,
    OLD_PASSWORD_INCORRECT,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    UNKNOWN_ERROR
}

data class AuthError(
    val code: ApiErrorCode,
    val message: String,
    val httpStatusCode: Int? = null
)