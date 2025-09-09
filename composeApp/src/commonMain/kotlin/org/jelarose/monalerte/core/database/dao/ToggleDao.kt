package org.jelarose.monalerte.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.jelarose.monalerte.core.database.entities.ToggleEntity

@Dao
interface ToggleDao {
    
    @Query("SELECT * FROM toggle_settings WHERE id = :toggleId")
    fun getToggle(toggleId: String): Flow<ToggleEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateToggle(toggle: ToggleEntity)
    
    @Query("DELETE FROM toggle_settings WHERE id = :toggleId")
    suspend fun deleteToggle(toggleId: String)
    
    @Query("SELECT * FROM toggle_settings")
    fun getAllToggles(): Flow<List<ToggleEntity>>
}