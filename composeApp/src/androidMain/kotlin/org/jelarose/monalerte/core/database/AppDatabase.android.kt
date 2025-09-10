package org.jelarose.monalerte.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.SQLiteConnection
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Migration de la version 1 vers 2 pour ajouter la table auth_tokens
val MIGRATION_1_2 = object : Migration(1, 2) {
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

object DatabaseHelper : KoinComponent {
    private val context: Context by inject()
    
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
        )
        .setDriver(BundledSQLiteDriver())
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration() // Fallback si la migration échoue
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return DatabaseHelper.getDatabaseBuilder()
}