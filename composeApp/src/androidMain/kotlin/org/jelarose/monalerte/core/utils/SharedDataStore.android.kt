package org.jelarose.monalerte.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

actual fun createDataStore(): DataStore<Preferences> {
    throw IllegalStateException("Use createDataStore(context) for Android")
}

fun createDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}