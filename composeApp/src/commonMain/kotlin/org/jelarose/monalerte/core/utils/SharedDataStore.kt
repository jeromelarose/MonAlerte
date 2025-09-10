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

class SharedDataStore(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        val TOGGLE_KEY = booleanPreferencesKey("test_toggle")
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
        val preferencesKey = intPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }
    
    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        val preferencesKey = intPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }.first()
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