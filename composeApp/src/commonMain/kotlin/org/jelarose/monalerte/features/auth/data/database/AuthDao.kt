package org.jelarose.monalerte.features.auth.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Dao
interface AuthDao {
    
    @Query("SELECT * FROM auth_tokens WHERE id = 1")
    fun getAuthTokenFlow(): Flow<AuthTokenEntity?>
    
    @Query("SELECT * FROM auth_tokens WHERE id = 1")
    suspend fun getAuthToken(): AuthTokenEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthToken(token: AuthTokenEntity)
    
    @Query("DELETE FROM auth_tokens")
    suspend fun clearAuthToken()
    
    @Query("SELECT EXISTS(SELECT 1 FROM auth_tokens WHERE id = 1 AND jwtToken IS NOT NULL AND jwtToken != '')")
    suspend fun hasValidToken(): Boolean
    
    @Query("UPDATE auth_tokens SET lastUpdated = :timestamp WHERE id = 1")
    suspend fun updateLastAccessed(timestamp: Long = Clock.System.now().toEpochMilliseconds())
}