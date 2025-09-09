package org.jelarose.monalerte.core.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jelarose.monalerte.core.database.AppDatabase
import org.jelarose.monalerte.core.database.entities.ToggleEntity

class ToggleRepository(private val database: AppDatabase) {
    
    companion object {
        const val ROOM_TOGGLE_ID = "room_toggle"
    }
    
    private val toggleDao = database.toggleDao()
    
    fun getRoomToggleState(): Flow<Boolean> {
        return toggleDao.getToggle(ROOM_TOGGLE_ID)
            .map { entity -> entity?.isEnabled ?: false }
    }
    
    suspend fun setRoomToggleState(enabled: Boolean) {
        toggleDao.insertOrUpdateToggle(
            ToggleEntity(
                id = ROOM_TOGGLE_ID,
                isEnabled = enabled
            )
        )
    }
}