package org.jelarose.monalerte.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toggle_settings")
data class ToggleEntity(
    @PrimaryKey
    val id: String,
    val isEnabled: Boolean
)