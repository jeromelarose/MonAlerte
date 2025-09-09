package org.jelarose.monalerte.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLsForDirectory(
                directory = NSDocumentDirectory,
                inDomains = NSUserDomainMask
            ).firstOrNull() as? NSURL
            requireNotNull(documentDirectory) { "Could not get document directory" }
            val dataStoreFile = documentDirectory.URLByAppendingPathComponent("settings.preferences_pb")
            requireNotNull(dataStoreFile) { "Could not create datastore file URL" }
            dataStoreFile.path!!.toPath()
        }
    )
}