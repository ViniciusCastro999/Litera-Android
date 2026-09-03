package com.litera.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.litera.app.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun observeByPost(postId: Long): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun countForPost(postId: Long): Int

    @Insert
    suspend fun insert(entity: CommentEntity)
}
