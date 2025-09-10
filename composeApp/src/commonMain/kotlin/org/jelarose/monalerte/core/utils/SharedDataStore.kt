package org.jelarose.monalerte.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import co.touchlab.kermit.Logger

class SharedDataStore(private val dataStore: DataStore<Preferences>) {
    
    private val logger = Logger.withTag("SharedDataStore")
    
    companion object {
        val TOGGLE_KEY = booleanPreferencesKey("test_toggle")
        
        // Watch Mode Settings Keys
        val WATCH_MODE_AUDIO_KEY = booleanPreferencesKey("watch_mode_audio_enabled")
        val WATCH_MODE_VIDEO_KEY = booleanPreferencesKey("watch_mode_video_enabled")
        val WATCH_MODE_SMS_TEMPLATE_KEY = stringPreferencesKey("watch_mode_sms_template")
    }
    
    val toggleState: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[TOGGLE_KEY] ?: false
        }
    
    suspend fun setToggleState(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[TOGGLE_KEY] = enabled
        }
    }
    
    // Watch Mode Audio Recording
    val watchModeAudioEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[WATCH_MODE_AUDIO_KEY] ?: false
        }
    
    suspend fun setWatchModeAudioEnabled(enabled: Boolean) {
        logger.d { "Setting watch mode audio enabled: $enabled" }
        dataStore.edit { preferences ->
            preferences[WATCH_MODE_AUDIO_KEY] = enabled
        }
    }
    
    // Watch Mode Video Recording
    val watchModeVideoEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[WATCH_MODE_VIDEO_KEY] ?: false
        }
    
    suspend fun setWatchModeVideoEnabled(enabled: Boolean) {
        logger.d { "Setting watch mode video enabled: $enabled" }
        dataStore.edit { preferences ->
            preferences[WATCH_MODE_VIDEO_KEY] = enabled
        }
    }
    
    // Watch Mode SMS Template
    val watchModeSmsTemplate: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[WATCH_MODE_SMS_TEMPLATE_KEY] ?: ""
        }
    
    suspend fun setWatchModeSmsTemplate(template: String) {
        logger.d { "Setting watch mode SMS template: ${template.take(50)}..." }
        dataStore.edit { preferences ->
            preferences[WATCH_MODE_SMS_TEMPLATE_KEY] = template
        }
    }
    
    // String operations
    suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }
    
    suspend fun getString(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[preferencesKey]
        }.first()
    }
    
    // Int operations
    suspend fun putInt(key: String, value: Int) {
        try {
            logger.d { "Storing int: $key = $value" }
            val preferencesKey = intPreferencesKey(key)
            dataStore.edit { preferences ->
                preferences[preferencesKey] = value
            }
            logger.d { "Successfully stored int: $key = $value" }
        } catch (e: Exception) {
            logger.e(e) { "Error storing int: $key = $value" }
            throw e
        }
    }
    
    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        return try {
            logger.d { "Retrieving int: $key (default: $defaultValue)" }
            val preferencesKey = intPreferencesKey(key)
            val result = dataStore.data.map { preferences ->
                preferences[preferencesKey] ?: defaultValue
            }.first()
            logger.d { "Retrieved int: $key = $result" }
            result
        } catch (e: Exception) {
            logger.e(e) { "Error retrieving int: $key, returning default: $defaultValue" }
            defaultValue
        }
    }
    
    // Remove key
    suspend fun removeKey(key: String) {
        val stringKey = stringPreferencesKey(key)
        val intKey = intPreferencesKey(key)
        val booleanKey = booleanPreferencesKey(key)
        
        dataStore.edit { preferences ->
            preferences.remove(stringKey)
            preferences.remove(intKey)
            preferences.remove(booleanKey)
        }
    }
}

// Factory function pour créer le DataStore
expect fun createDataStore(): DataStore<Preferences>