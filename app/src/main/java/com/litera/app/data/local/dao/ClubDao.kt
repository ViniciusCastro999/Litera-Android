package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.litera.app.data.local.entity.ClubEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {

    @Query("SELECT * FROM clubs ORDER BY id ASC")
    fun observeAll(): Flow<List<ClubEntity>>

    @Query("SELECT * FROM clubs WHERE id = :id")
    fun observeById(id: Long): Flow<ClubEntity?>

    @Query("SELECT * FROM clubs WHERE id = :id")
    suspend fun getById(id: Long): ClubEntity?

    @Query("SELECT COUNT(*) FROM clubs")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(entities: List<ClubEntity>)

    @Update
    suspend fun update(entity: ClubEntity)
}
