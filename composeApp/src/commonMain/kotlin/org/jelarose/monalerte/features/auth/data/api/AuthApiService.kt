package org.jelarose.monalerte.features.auth.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jelarose.monalerte.features.auth.domain.models.*

class AuthApiService(private val httpClient: HttpClient) {
    
    // Base URL is now configured in HttpClient DefaultRequest
    
    /**
     * Authenticate user with email and password
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = httpClient.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val loginResponse: LoginResponse = response.body()
                    Result.success(loginResponse)
                }
                HttpStatusCode.Unauthorized -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.INVALID_CREDENTIALS,
                                message = "Invalid email or password",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                HttpStatusCode.NotFound -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.EMAIL_NOT_FOUND,
                                message = "Email not found",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                else -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.UNKNOWN_ERROR,
                                message = "Login failed: ${response.status}",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthError(
                        code = ApiErrorCode.NETWORK_ERROR,
                        message = e.message ?: "Network error occurred"
                    )
                )
            )
        }
    }
    
    /**
     * Register new user
     */
    suspend fun register(request: RegisterRequest): Result<LoginResponse> {
        return try {
            // Debug logging like original Android app
            println("=== REGISTER REQUEST ===")
            println("Email: ${request.email}")
            println("Password length: ${request.password.length}")
            println("Accepted policy version: ${request.acceptedPolicyVersion}")
            println("Note: firstName, lastName, phoneNumber excluded to match original Android app")
            
            val response = httpClient.post("/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("Register response status: ${response.status}")
            println("Register response headers: ${response.headers}")
            
            when (response.status) {
                HttpStatusCode.Created -> {
                    val loginResponse: LoginResponse = response.body()
                    Result.success(loginResponse)
                }
                HttpStatusCode.BadRequest -> {
                    // Parse the error response to get the actual error code
                    try {
                        val errorBody: String = response.body()
                        println("Error response body: $errorBody")
                        
                        // Check if it's EMAIL_ALREADY_EXISTS error
                        if (errorBody.contains("EMAIL_ALREADY_EXISTS")) {
                            Result.failure(
                                AuthException(
                                    AuthError(
                                        code = ApiErrorCode.EMAIL_ALREADY_EXISTS,
                                        message = "Cet email est déjà enregistré et vérifié.",
                                        httpStatusCode = response.status.value
                                    )
                                )
                            )
                        } else if (errorBody.contains("MISSING_FIELDS") || errorBody.contains("champ")) {
                            Result.failure(
                                AuthException(
                                    AuthError(
                                        code = ApiErrorCode.MISSING_FIELDS,
                                        message = "Missing required fields",
                                        httpStatusCode = response.status.value
                                    )
                                )
                            )
                        } else {
                            Result.failure(
                                AuthException(
                                    AuthError(
                                        code = ApiErrorCode.UNKNOWN_ERROR,
                                        message = errorBody,
                                        httpStatusCode = response.status.value
                                    )
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Result.failure(
                            AuthException(
                                AuthError(
                                    code = ApiErrorCode.MISSING_FIELDS,
                                    message = "Missing required fields",
                                    httpStatusCode = response.status.value
                                )
                            )
                        )
                    }
                }
                HttpStatusCode.Conflict -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.EMAIL_ALREADY_EXISTS,
                                message = "Email already exists",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                else -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.UNKNOWN_ERROR,
                                message = "Registration failed: ${response.status}",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthError(
                        code = ApiErrorCode.NETWORK_ERROR,
                        message = e.message ?: "Network error occurred"
                    )
                )
            )
        }
    }
    
    /**
     * Reset password for given email
     */
    suspend fun forgotPassword(request: ResetPasswordRequest): Result<Unit> {
        return try {
            val response = httpClient.post("/forgot-password") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                HttpStatusCode.NotFound -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.EMAIL_NOT_FOUND,
                                message = "Email not found",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                else -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.UNKNOWN_ERROR,
                                message = "Password reset failed: ${response.status}",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthError(
                        code = ApiErrorCode.NETWORK_ERROR,
                        message = e.message ?: "Network error occurred"
                    )
                )
            )
        }
    }
    
    /**
     * Change password for authenticated user
     */
    suspend fun changePassword(request: ChangePasswordRequest, authToken: String): Result<Unit> {
        return try {
            val response = httpClient.post("/auth/change-password") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $authToken")
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                HttpStatusCode.BadRequest -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.OLD_PASSWORD_INCORRECT,
                                message = "Old password is incorrect",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                HttpStatusCode.Unauthorized -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.INVALID_CREDENTIALS,
                                message = "Invalid authentication token",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                else -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.UNKNOWN_ERROR,
                                message = "Password change failed: ${response.status}",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthError(
                        code = ApiErrorCode.NETWORK_ERROR,
                        message = e.message ?: "Network error occurred"
                    )
                )
            )
        }
    }
    
    /**
     * Verify JWT token and optionally accept policy
     */
    suspend fun verifyToken(request: AcceptPolicyRequest?, authToken: String): Result<Unit> {
        return try {
            val response = httpClient.post("/auth/verify-token") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $authToken")
                if (request != null) {
                    setBody(request)
                }
            }
            
            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                HttpStatusCode.Unauthorized -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.INVALID_CREDENTIALS,
                                message = "Invalid or expired token",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
                else -> {
                    Result.failure(
                        AuthException(
                            AuthError(
                                code = ApiErrorCode.UNKNOWN_ERROR,
                                message = "Token verification failed: ${response.status}",
                                httpStatusCode = response.status.value
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                AuthException(
                    AuthError(
                        code = ApiErrorCode.NETWORK_ERROR,
                        message = e.message ?: "Network error occurred"
                    )
                )
            )
        }
    }
}

/**
 * Custom exception for authentication errors
 */
class AuthException(val authError: AuthError) : Exception(authError.message)