package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.litera.app.data.local.entity.ShelfBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {

    @Query("SELECT * FROM shelf_books ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ShelfBookEntity>>

    @Query("SELECT * FROM shelf_books WHERE volumeId = :volumeId")
    fun observeById(volumeId: String): Flow<ShelfBookEntity?>

    @Query("SELECT * FROM shelf_books WHERE volumeId = :volumeId")
    suspend fun getById(volumeId: String): ShelfBookEntity?

    @Upsert
    suspend fun upsert(entity: ShelfBookEntity)

    @Query("DELETE FROM shelf_books WHERE volumeId = :volumeId")
    suspend fun deleteById(volumeId: String)

    @Delete
    suspend fun delete(entity: ShelfBookEntity)
}
