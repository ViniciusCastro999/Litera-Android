package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.litera.app.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun observeAll(): Flow<List<FocusSessionEntity>>

    @Insert
    suspend fun insert(entity: FocusSessionEntity)
}
