package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.litera.app.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE volumeId = :volumeId ORDER BY createdAt DESC")
    fun observeByVolume(volumeId: String): Flow<List<NoteEntity>>

    @Upsert
    suspend fun upsert(entity: NoteEntity)

    @Delete
    suspend fun delete(entity: NoteEntity)
}
