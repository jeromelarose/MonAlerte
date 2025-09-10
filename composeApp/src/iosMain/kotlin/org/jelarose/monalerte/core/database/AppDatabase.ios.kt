package org.jelarose.monalerte.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.SQLiteConnection
import platform.Foundation.NSHomeDirectory

// Migration de la version 1 vers 2 pour ajouter la table auth_tokens
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.prepare("""
            CREATE TABLE IF NOT EXISTS auth_tokens (
                id INTEGER PRIMARY KEY NOT NULL,
                jwtToken TEXT NOT NULL,
                userEmail TEXT NOT NULL,
                tokenExpiryTime INTEGER,
                refreshToken TEXT,
                lastUpdated INTEGER NOT NULL
            )
        """.trimIndent()).use { statement ->
            statement.step()
        }
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = NSHomeDirectory() + "/${AppDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    )
    .setDriver(BundledSQLiteDriver())
    .addMigrations(MIGRATION_1_2)
    .fallbackToDestructiveMigration(true) // Fallback si la migration échoue
}