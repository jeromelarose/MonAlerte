package org.jelarose.monalerte.features.auth.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "auth_tokens")
data class AuthTokenEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for current auth token
    val jwtToken: String,
    val userEmail: String,
    val tokenExpiryTime: Long? = null,
    val refreshToken: String? = null,
    val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
)