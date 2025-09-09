package org.jelarose.monalerte.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
}

// Factory function pour créer le DataStore
expect fun createDataStore(): DataStore<Preferences>