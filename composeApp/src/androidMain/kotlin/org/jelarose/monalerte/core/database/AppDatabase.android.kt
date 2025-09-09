package org.jelarose.monalerte.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DatabaseHelper : KoinComponent {
    private val context: Context by inject()
    
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
        ).setDriver(BundledSQLiteDriver())
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return DatabaseHelper.getDatabaseBuilder()
}