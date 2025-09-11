package org.jelarose.monalerte.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.jelarose.monalerte.core.database.dao.ToggleDao
import org.jelarose.monalerte.core.database.entities.ToggleEntity
import org.jelarose.monalerte.features.auth.data.database.AuthDao
import org.jelarose.monalerte.features.auth.data.database.AuthTokenEntity
import org.jelarose.monalerte.features.contacts.data.database.ContactDao
import org.jelarose.monalerte.features.contacts.data.database.ContactEntity
import org.jelarose.monalerte.features.contacts.data.database.ContactListEntity

@Database(
    entities = [
        ToggleEntity::class, 
        AuthTokenEntity::class,
        ContactListEntity::class,
        ContactEntity::class
    ],
    version = 3,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toggleDao(): ToggleDao
    abstract fun authDao(): AuthDao
    abstract fun contactDao(): ContactDao
    
    companion object {
        const val DATABASE_NAME = "monalerte_database.db"
    }
}

// Constructor pour Room KMP
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

// Factory function pour créer la base de données
expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>