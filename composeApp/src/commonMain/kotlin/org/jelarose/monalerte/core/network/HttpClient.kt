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
            })
        }
        
        // Configuration des logs
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
        
        // Configuration par défaut
        install(DefaultRequest) {
            // Headers par défaut si nécessaire
        }
        
        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}