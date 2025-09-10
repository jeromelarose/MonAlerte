package org.jelarose.monalerte.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Configuration du client HTTP Ktor pour les appels API
 */
fun createHttpClient(): HttpClient {
    return HttpClient {
        // Configuration JSON
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true // Force inclusion of null fields to match server expectations
            })
        }
        
        // Configuration des logs
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL // Match original Android app logging level
        }
        
        // Configuration par défaut avec base URL
        install(DefaultRequest) {
            url("https://51.75.120.88.nip.io/")
            headers.append("Accept", "application/json")
            headers.append("Content-Type", "application/json")
        }
        
        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}