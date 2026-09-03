package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.litera.app.data.local.entity.ReadingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingGoalDao {

    @Query("SELECT * FROM reading_goals ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ReadingGoalEntity>>

    @Query("SELECT * FROM reading_goals WHERE id = :id")
    suspend fun getById(id: Long): ReadingGoalEntity?

    @Upsert
    suspend fun upsert(entity: ReadingGoalEntity): Long

    @Delete
    suspend fun delete(entity: ReadingGoalEntity)
}
